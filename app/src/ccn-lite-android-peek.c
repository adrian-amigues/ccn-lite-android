//
// Created by adrian on 2016-09-15.
//

#include <string.h>

#include "util/ccnl-common-for-android.c"
#include "util/ccnl-socket.c"


#include "util/ccn-lite-pktdump-android.c"




#define USE_URI_TO_PREFIX

// Function prototypes
int udp_open_by_max();
int ccnl_mgmt_discover(struct ccnl_relay_s *ccnl, struct ccnl_buf_s *orig,
        struct ccnl_prefix_s *prefix, struct ccnl_face_s *from);
int frag_cb(struct ccnl_relay_s *relay, struct ccnl_face_s *from,
        unsigned char **data, int *len);

// Global variables
unsigned char out[8*CCNL_MAX_PACKET_SIZE];
int outlen;


// Main function for peeking with android
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


    time(&curtime);

    DEBUGMSG(TRACE, "using udp address %s/%d\n", addr, port);
    sprintf(response, " using udp address %s/%d\n", addr, port);

    // Getting the suite integer value
    suite = ccnl_str2suite(suiteStr);
    isContent = ccnl_suite2isContentFunc(suite);
    if (!isContent) {
        exit(-1);
    }

    // Transforming prefix if needed
    strcpy(uri_static, uri);
    prefix = ccnl_URItoPrefix(uri_static, suite, NULL, NULL);
    if (!prefix) {
        DEBUGMSG(ERROR, "no URI found, aborting\n");
        return -1;
    }
    sprintf(response, "%s prefix <%s> became %s\n", response, uri, ccnl_prefix_to_path(prefix));


    // Make the interest
    len = ccntlv_mkInterest(prefix, &nonce, out, sizeof(out));
    sprintf(response, "%s interest has %d bytes\n", response, len);

    // Initialize network functionalities
    struct sockaddr_in *si = (struct sockaddr_in*) &sa;
    si->sin_family = PF_INET;
    si->sin_addr.s_addr = inet_addr(addr);
    si->sin_port = htons(port);
    sock = udp_open();
    // sock = udp_open_by_max();
    socksize = sizeof(struct sockaddr_in);

    // Sending Interest
    sprintf(response, "%s sendto: sock=%d, len out=%d, out=%s\n", response, sock, strlen(out), out);
    rc = sendto(sock, out, len, 0, (struct sockaddr*)&sa, socksize);
    if (rc < 0) {
        perror("sendto");
        return "rc < 0";
    }
    DEBUGMSG(DEBUG, "sendto returned %d\n", rc);
    sprintf(response, "%s sendto returned %d\n", response, rc);

    for (;;) { // wait for a content pkt (ignore interests)
        unsigned char *cp = out;
        int enc, suite2, len2;
        DEBUGMSG(TRACE, "  waiting for packet\n");
        sprintf(response, "%s waiting for packet\n", response);

        if (block_on_read(sock, wait) <= 0) // timeout
            break;
        sprintf(response, "%s between block_on_read and recv\n", response);
        len = recv(sock, out, sizeof(out), 0);

        DEBUGMSG(DEBUG, "received %d bytes\n", len);
        sprintf(response, "%s received %d bytes\n", response, len);
/*
        {
            int fd = open("incoming.bin", O_WRONLY|O_CREAT|O_TRUNC, 0700);
            write(fd, out, len);
            close(fd);
        }
*/
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
        if (isFragment && isFragment(cp, len2)) {
            int t;
            int len3;
            DEBUGMSG(DEBUG, "  fragment, %d bytes\n", len2);
            sprintf(response, "%s fragment, %d bytes\n", response, len2);
            return response;
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
                /*
                rc = ccnl_frag_RX_Sequenced2015(frag_cb, NULL, &dummyFace,
                                  4096, hp->fill[0] >> 6,
                                  ntohs(*(uint16_t*) hp->fill) & 0x03fff,
                                  &cp, (int*) &len2);
                */
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
                /*
                fprintf(stderr, "t=%d len=%d\n", t, len2);
                if (ccnl_iottlv_dehead(&cp, &len, &t, &len2))
                    continue;
                */
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
                /*
                rc = ccnl_frag_RX_Sequenced2015(frag_cb, NULL, &dummyFace,
                         4096, tmp >> 14, tmp & 0x7ff, &cp, (int*) &len2);
                */
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

#endif

/*
    {
        int fd = open("incoming.bin", O_WRONLY|O_CREAT|O_TRUNC);
        write(fd, out, len);
        close(fd);
    }
*/
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
        
    }


    sprintf(response, "%s Returning response, len = %d, strlen(out) = %d\n", response, len, strlen(out));
    sprintf(response, "%s out = %s\n", response, pktdump_android(out, len, format, suite));
    return response;
done:
    close(sock);
    return response; // avoid a compiler warning
}

// Max's functions
int
udp_open_by_max()
{
    int s;
    struct sockaddr_in si;

    int broadcastEnable=1;



    s = socket(PF_INET, SOCK_DGRAM, 0);
    if (s < 0) {
        perror("udp socket");
        exit(1);
    }

    int ret=setsockopt(s, SOL_SOCKET, SO_BROADCAST, &broadcastEnable, sizeof(broadcastEnable));

    if (ret == -1) {
        DEBUGMSG(TRACE, "Could not set broadcastmode");
    }    
    si.sin_addr.s_addr = INADDR_ANY;
    si.sin_port = htons(0);
    si.sin_family = PF_INET;
    

    if (bind(s, (struct sockaddr *)&si, sizeof(si)) < 0) {
        perror("udp sock bind");
        exit(1);
    }

    return s;
}

int
ccnl_mgmt_discover(struct ccnl_relay_s *ccnl, struct ccnl_buf_s *orig,
               struct ccnl_prefix_s *prefix, struct ccnl_face_s *from)
{
    DEBUGMSG(TRACE, "ccnl_mgmt_discover\n");

    unsigned char out[CCNL_MAX_PACKET_SIZE];
    int sock = 0;
    int socksize;
    int rc;
    char buf[100];
    char *pfx;
    int len=0;
    struct ccnl_prefix_s *prefixfind;
    int suite = CCNL_SUITE_CCNB;
    time_t curtime;
    time(&curtime);
    struct sockaddr sa;
    uint32_t nonce = (uint32_t) difftime(curtime, 0);
    pfx = "/ccnx//find";
    strcpy(buf, pfx);
    int port;
    char *addr = NULL;

    DEBUGMSG(TRACE, "Parsing UDP");

    addr = "255.255.255.255";
    port = 9999;

    DEBUGMSG(TRACE, "using udp address %s/%d\n", addr, port);


    prefixfind = ccnl_URItoPrefix(buf,
                              suite,
                              NULL,
                              NULL);


    DEBUGMSG(TRACE, "prefixfind: %s\n", ccnl_prefix_to_path(prefixfind));

    len = ccntlv_mkInterest(prefixfind,
                                (int*)&nonce,
                                out, CCNL_MAX_PACKET_SIZE);

    
    struct sockaddr_in *si = (struct sockaddr_in*) &sa;
    si->sin_family = PF_INET;
    si->sin_addr.s_addr = inet_addr(addr);
    si->sin_port = htons(port);
    sock = udp_open_by_max();
    socksize = sizeof(struct sockaddr_in);

    rc = sendto(sock, out, len, 0, (struct sockaddr*)&sa, socksize);
    if (rc < 0) {
        perror("sendto");
    }
    DEBUGMSG(DEBUG, "sendto returned %d\n", rc);


    printf("%d", len);


    return 0;
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
