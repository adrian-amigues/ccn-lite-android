/*
 * @f ccn-lite-android-peek.c
 * @b native code (library) for Android devices to communicate Interests and recieve 
 * content objects back
 *
 * Copyright (C) 2015, Christian Tschudin, University of Basel
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * File history:
 * 2016-09-15 created by the Uppsala University CSproject 2016 team
 */

#include <string.h>
#include <time.h>

#include "util/ccnl-common-for-android.c"
#include "util/ccnl-socket.c"
#include "util/ccn-lite-pktdump-android.c"

#define USE_URI_TO_PREFIX

// Function prototypes
int frag_cb(struct ccnl_relay_s *relay, struct ccnl_face_s *from,
        unsigned char **data, int *len);
double timeDiff(time_t begin);

// Global variables
unsigned char out[8*CCNL_MAX_PACKET_SIZE];
int outlen;


/**
 * Main function for the communication with CCN
 * Behavior from ccn-lite-peek.c
 *
 * Input: 
 *  suiteStr = which suite to use (ccnx2015, ndn2013)
 *  addr = the udp address to send the interest to
 *  port = the port to use
 *  uri = the name of the object to querry for
 *
 * Output: the content of the content-object
 * 
 * The function creates an Interest and sends it to the specified udp address,
 * it waits for the return of a content packet and prints it
 */
char* ccnl_android_peek(char* suiteStr, char* addr, int port, char* uri) {
    static char uri_static[100];
    static char response[400];
    static ccnl_isContentFunc isContent;
    static ccnl_isFragmentFunc isFragment;
    struct ccnl_prefix_s *prefix;
    struct ccnl_face_s dummyFace;
    unsigned int chunknum = UINT_MAX;
    int len, socksize, rc, suite;
    int sock = 0;
    int format = 2; // To print just the content of the returned content object, don't change to lower
    time_t curtime;
    uint32_t nonce = (uint32_t) difftime(curtime, 0);
    char *path;
    struct sockaddr sa;
    float wait = 3.0;
    clock_t begin = clock();

    time(&curtime);

    DEBUGMSG(TRACE, "using udp address %s/%d\n", addr, port);
    // sprintf(response, " using udp address %s/%d\n", addr, port);

    // Getting the suite integer value
    suite = ccnl_str2suite(suiteStr);
    isContent = ccnl_suite2isContentFunc(suite);
    if (!isContent) {
        return "Suite is not valid\n";
    }

    // Transforming prefix if needed
    strcpy(uri_static, uri);
    prefix = ccnl_URItoPrefix(uri_static, suite, NULL, NULL);
    if (!prefix) {
        DEBUGMSG(ERROR, "no URI found, aborting\n");
        return "no URI found, aborting\n";
    }
    // sprintf(response, "%s prefix <%s> became %s\n", response, uri, ccnl_prefix_to_path(prefix));


    // Make the interest
    len = ccntlv_mkInterest(prefix, &nonce, out, sizeof(out));
    // sprintf(response, "%s interest has %d bytes\n", response, len);

    // Initialize network functionalities
    struct sockaddr_in *si = (struct sockaddr_in*) &sa;
    si->sin_family = PF_INET;
    si->sin_addr.s_addr = inet_addr(addr);
    si->sin_port = htons(port);
    sock = udp_open();
    socksize = sizeof(struct sockaddr_in);

    // Sending Interest
    // sprintf(response, "%s sendto: sock=%d, len out=%d, out=%s\n", response, sock, strlen(out), out);
    rc = sendto(sock, out, len, 0, (struct sockaddr*)&sa, socksize);
    if (rc < 0) {
        perror("sendto");
        return "rc < 0";
    }
    DEBUGMSG(DEBUG, "sendto returned %d\n", rc);
    // sprintf(response, "%s sendto returned %d\n", response, rc);

    for (;;) { // wait for a content pkt (ignore interests)
        unsigned char *cp = out;
        int enc, suite2, len2;
        DEBUGMSG(TRACE, "  waiting for packet\n");
        sprintf(response, "%s [%f] - before block_on_read\n", response, timeDiff(begin));

        if (block_on_read(sock, wait) <= 0) {// timeout
            sprintf(response, "%s bor <= 0\n", response);
            break;
        }
        sprintf(response, "%s [%f] - between block_on_read and recv\n", response, timeDiff(begin));
        len = recv(sock, out, sizeof(out), 0);

        DEBUGMSG(DEBUG, "received %d bytes\n", len);
        sprintf(response, "%s [%f] - received %d bytes\n", response, timeDiff(begin), len);

        suite2 = -1;
        len2 = len;
        while (!ccnl_switch_dehead(&cp, &len2, &enc))
            suite2 = ccnl_enc2suite(enc);
        if (suite2 != -1 && suite2 != suite) {
            DEBUGMSG(DEBUG, "  unknown suite %d\n", suite);
            sprintf(response, "%s unknown suite %d\n", response, suite);

            continue;
        }

#ifdef USE_FRAG
        // sprintf(response, "%s USE_FRAG\n", response);
        if (isFragment && isFragment(cp, len2)) {
            int t;
            int len3;
            DEBUGMSG(DEBUG, "  fragment, %d bytes\n", len2);
            sprintf(response, "%s fragment, %d bytes\n", response, len2);
            return response; // TODO: remove that and test fragmenting
            switch(suite) {
            case CCNL_SUITE_CCNTLV: {
                struct ccnx_tlvhdr_ccnx2015_s *hp;
                hp = (struct ccnx_tlvhdr_ccnx2015_s *) out;
                cp = out + sizeof(*hp);
                len2 -= sizeof(*hp);
                if (ccnl_ccntlv_dehead(&cp, &len2, (unsigned*)&t, (unsigned*) &len3) < 0 ||
                    t != CCNX_TLV_TL_Fragment) {
                    DEBUGMSG(ERROR, "  error parsing fragment\n");
                    continue;
                }

                rc = ccnl_frag_RX_BeginEnd2015(frag_cb, NULL, &dummyFace,
                                  4096, hp->fill[0] >> 6,
                                  ntohs(*(uint16_t*) hp->fill) & 0x03fff,
                                  &cp, (int*) &len3);
                break;
            }
            case CCNL_SUITE_IOTTLV: {
                uint16_t tmp;

                if (ccnl_iottlv_dehead(&cp, &len2, (unsigned*) &t, &len3)) { // IOT_TLV_Fragment
                    DEBUGMSG(VERBOSE, "problem parsing fragment\n");
                    continue;
                }

                DEBUGMSG(VERBOSE, "t=%d, len=%d\n", t, len3);
                sprintf(response, "%s t=%d, len=%d\n", response, t, len3);
                if (t == IOT_TLV_F_OptFragHdr) { // skip it for the time being
                    cp += len3;
                    len2 -= len3;
                    if (ccnl_iottlv_dehead(&cp, &len2, (unsigned*) &t, &len3))
                        continue;
                }
                if (t != IOT_TLV_F_FlagsAndSeq || len3 < 2) {
                    DEBUGMSG(DEBUG, "  no flags and seqrn found (%d)\n", t);
                    continue;
                }
                tmp = ntohs(*(uint16_t*) cp);
                cp += len3;
                len2 -= len3;

                if (ccnl_iottlv_dehead(&cp, &len2, (unsigned*) &t, &len3)) {
                    DEBUGMSG(DEBUG, "  cannot parse frag payload\n");
                    continue;
                }
                DEBUGMSG(DEBUG, "  fragment payload len=%d\n", len3);
                if (t != IOT_TLV_F_Data) {
                    DEBUGMSG(DEBUG, "  no payload (%d)\n", t);
                    continue;
                }

                rc = ccnl_frag_RX_BeginEnd2015(frag_cb, NULL, &dummyFace,
                         4096, tmp >> 14, tmp & 0x3fff, &cp, (int*) &len3);
                fprintf(stderr, "--\n");
                break;
            }
            default:
                continue;
            }
            if (!outlen)
                continue;
            len = outlen;
        }

#endif // USE_FRAG

        rc = isContent(out, len);
        if (rc < 0) {
            DEBUGMSG(ERROR, "error when checking type of packet\n");
            sprintf(response, "%s error when checking type of packet\n", response);
            goto done;
        }
        if (rc == 0) { // it's an interest, ignore it
            DEBUGMSG(WARNING, "skipping non-data packet\n");
            continue;
        }
        if (rc == 1) { // Added by Adrian
            sprintf(response, "%s rc == 1\n", response);
            break;
        }
    }


    // sprintf(response, "%s Returning response, len = %d, strlen(out) = %d\n", response, len, strlen(out));
    // sprintf(response, "%s out = %s\n", response, pktdump_android(out, len, format, suite));
    sprintf(response, "%s [%f] - return response\n", response, timeDiff(begin));
    sprintf(response, "%s\n->%s\n", response, pktdump_android(out, len, format, suite));
    sprintf(response, "->%s\n", pktdump_android(out, len, format, suite));
    return response;
done:
    close(sock);
    return response; // avoid a compiler warning
}

// necessary function from ccn-lite-peek.c
int
frag_cb(struct ccnl_relay_s *relay, struct ccnl_face_s *from,
        unsigned char **data, int *len)
{
    DEBUGMSG(INFO, "frag_cb\n");

    memcpy(out, *data, *len);
    outlen = *len;
    return 0;
}

double timeDiff(time_t begin) {
    return (double)(clock() - begin) / CLOCKS_PER_SEC;
}