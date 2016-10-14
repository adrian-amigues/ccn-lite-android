/*
 * @f util/ccn-lite-android-mkC.c
 *
 * Copyright (C) 2013-15, Christian Tschudin, University of Basel
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
 * 2016-10-13  created by Johan Snider
 */

//different suites
//TODO: maybe these can be taken out
#define USE_SUITE_CCNB
#define USE_SUITE_CCNTLV //this one
#define USE_SUITE_CISTLV
#define USE_SUITE_IOTTLV
#define USE_SUITE_NDNTLV

#define USE_HMAC256
#define USE_SIGNATURES

#define NEEDS_PACKET_CRAFTING

//these will be in util
//#include "util/ccnl-common.c"
//#include "util/ccnl-crypto.c"

//maybe don't need these 5 imports, probably needs socket

#include <string.h>
#include <time.h>
//#include "util/ccnl-common-for-android.c"
//#include "util/ccnl-socket.c"
//#include "util/ccn-lite-pktdump-android.c"



// ----------------------------------------------------------------------

char *private_key_path; //not used in file
char *witness; // used with w flag?

// ----------------------------------------------------------------------

char* ccnl_android_mkC(char* suiteStr, char* addr, int port, char* uri, char* body_param) {
    unsigned char body[64 * 1024]; //body of content object
    unsigned char out[65 * 1024]; //this should be the result
    unsigned char *publisher = out;
    char *outfname = 0;
    unsigned int chunknum = UINT_MAX, lastchunknum = UINT_MAX;
    int f, len, opt, plen, offs = 0;
    struct ccnl_prefix_s *prefix; //name of content object
    int suite = CCNL_SUITE_DEFAULT;
    struct key_s *keys = NULL; //not exactly sure

    static char uri_static[100];
    struct sockaddr sa;
    int sock = 0;
    int socksize, rc;


    /* not sure about this n.s.a.t
      case 'k':
         keys = load_keys_from_file(optarg);
         break; */
    /* n.s.a.t
    case 'l':
        lastchunknum = atoi(optarg);
        break;
    case 'n':
        chunknum = atoi(optarg);
        break;
    case 'o':
        outfname = optarg;
        break;
    */

    /* nsat
    case 'p':
        publisher = (unsigned char*) optarg;
        plen = unescape_component((char*) publisher);
        if (plen != 32) {
            DEBUGMSG(ERROR,
              "publisher key digest has wrong length (%d instead of 32)\n",
              plen);
            exit(-1);
        }
        break;
        */
    //case 's':
    // this we need!
    suite = ccnl_str2suite(suiteStr);
    if (!ccnl_isSuite(suite)) {
        return "Suite is not valid\n";
    }
    /* nsat
    case 'w':
        witness = optarg;
        break;
        */
    //put content parameter to
    //TODO: fix this
    // body = body_param; //maybe unneccassary

    memset(out, 0, sizeof(out)); //writes zeros to out??

    //set content object name
    strcpy(uri_static, uri);
    prefix = ccnl_URItoPrefix(uri_static, suite, NULL, NULL); //could have had chunk things here
    if (!prefix) {
        return "no URI found, aborting\n";
    }

    //this function takes prefix, body and length, something and builds the result into string object "out"
    //for CCNL_SUITE_CCNB
    //len = ccnl_ccnb_fillContent(prefix, body, len, NULL, out);

    //from USE_SUITE_CCNTLV
    len = strlen(body_param) + 1; //TODO: double check this
    offs = CCNL_MAX_PACKET_SIZE; //maybe this
    len = ccnl_ccntlv_prependContentWithHdr(prefix, body, len, NULL, NULL, &offs, out);

    //so now the content of the content object should be in "out", now we need to send it to the local host router

    //now do network things
    struct sockaddr_in *si = (struct sockaddr_in *) &sa;
    si->sin_family = PF_INET;
    si->sin_addr.s_addr = inet_addr(addr);
    si->sin_port = htons(port);
    sock = udp_open();
    socksize = sizeof(struct sockaddr_in);

    // Sending Interest
    // sprintf(response, "%s sendto: sock=%d, len out=%d, out=%s\n", response, sock, strlen(out), out);
    rc = sendto(sock, out, len, 0, (struct sockaddr *) &sa, socksize);
    //TODO: needs testing on network side to confirm content object shows up in cache

    DEBUGMSG(DEBUG, "sendto returned %d\n", rc);
    // sprintf(response, "%s sendto returned %d\n", response, rc);

    if (rc < 0) {
        perror("sendto");
        return "rc < 0";
    } else {
        //return confirmation message
        return "1";
    }
}