#include <jni.h>
#include <string>
#include <iostream>
#include <cerrno>
#include <cstdio>
#include <cstring>
#include <net/if.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>


#include <netlink/genl/genl.h>
#include <netlink/genl/family.h>
#include <netlink/genl/ctrl.h>
#include <netlink/msg.h>
#include <netlink/attr.h>

#include <netlink-types.h>

#include "nl80211.h"

#define COMMAND(section, name, args, cmd, flags, handler, help)    \
    __COMMAND(&(__section ## _ ## section), name, #name, args, cmd, flags, 0,  handler, help, NULL)


#define __COMMAND(_section, _symname, _name, _args, _nlcmd, _flags, _hidden, _handler, _help, _sel)\
    static struct cmd                        \
    __cmd ## _ ## _symname ## _ ## _handler ## _ ## _nlcmd ## _  ## _ ## _hidden\
    __attribute__((used)) __attribute__((section("__cmd")))    = {    \
        .name = (_name),                    \
        .args = (_args),                    \
        .cmd = (_nlcmd),                    \
        .nl_msg_flags = (_flags),                \
        .hidden = (_hidden),                    \
        .handler = (_handler),                    \
        .help = (_help),                    \
        .parent = _section,                    \
        .selector = (_sel),                    \
    }


#define out_free_msg()\
nlmsg_free(msg); \
std::string hello1 = "ERRORRRRRRRRRRRRRRRRRRRRRRRRRRR";\
return env->NewStringUTF(hello1.c_str());

#define out()\
nl_cb_put(cb);\
out_free_msg()

#define nla_put_failure()\
std::string hello1 = "ERRORRRRRRRRRRRRRRRRRRRRRRRRRRR";\
return env->NewStringUTF(hello1.c_str());


struct cmd {
    const char *name;
    const char *args;
    const char *help;
    const enum nl80211_commands cmd;
    int nl_msg_flags;
    int hidden;

    /*
     * The handler should return a negative error code,
     * zero on success, 1 if the arguments were wrong
     * and the usage message should and 2 otherwise.
     */
    int (*handler)(struct nl80211_state *state,
                   struct nl_cb *cb,
                   struct nl_msg *msg,
                   int argc, char **argv);

    const struct cmd *(*selector)(int argc, char **argv);

    const struct cmd *parent;
};

struct cmd __section_survey __attribute__((used))__attribute__((section("__cmd"))) = {.name=("survey"), .hidden=1,};

struct nl80211_state {
    struct nl_sock *nl_sock;
    int nl80211_id;
};

int nl80211_init(nl80211_state *state);

static int get_survey_info(struct nl_msg *msg, void *arg);

static int handle_survey_dump(struct nl80211_state *state,
                              struct nl_cb *cb,
                              struct nl_msg *msg,
                              int argc, char **argv);

static int error_handler(struct sockaddr_nl *nla, struct nlmsgerr *err,
                         void *arg);

static int finish_handler(struct nl_msg *msg, void *arg);

static int ack_handler(struct nl_msg *msg, void *arg);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_vrem_wifianalyzer_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    struct nl80211_state nlstate;
    int err = nl80211_init(&nlstate);

    printf("%d\n", err);
    fflush(stdout);
    struct nl_cb *cb;
    struct nl_cb *s_cb;
    struct nl_msg *msg;

    uint32_t devidx = 1;

    msg = nlmsg_alloc();
    if (!msg) {
        fprintf(stderr, "failed to allocate netlink message\n");
        std::string hello = "ERRORRRRRRRRRRRRRRRRRRRRRRRRRRR";
        return env->NewStringUTF(hello.c_str());

    }


//    struct if_nameindex* all_wifi=if_nameindex();

    cb = nl_cb_alloc(NL_CB_DEFAULT);
    s_cb = nl_cb_alloc(NL_CB_DEFAULT);
    if (!cb || !s_cb) {
        fprintf(stderr, "failed to allocate netlink callbacks\n");
        out_free_msg()
    }

    genlmsg_put(msg, 0, 0, nlstate.nl80211_id, 0, NLM_F_DUMP, NL80211_CMD_GET_SURVEY, 0);


    uint32_t __tmp = devidx;
    if (nla_put(msg, NL80211_ATTR_IFINDEX, sizeof(uint32_t), &__tmp) < 0) {
        nla_put_failure()
    }

    err = handle_survey_dump(&nlstate, cb, msg, 0, NULL);
    if (err) {
        out()
    }

    printf("FUCK!!!!!!!!!!!!!!!!\n");
    fflush(stdout);

    nl_socket_set_cb(nlstate.nl_sock, s_cb);

    if (nlstate.nl_sock->s_fd < 0) {
        std::cerr << "FUCK!!!!!!!!!!!!!!!!" << std::endl;
    }

    err = nl_send_auto_complete(nlstate.nl_sock, msg);
    if (err < 0) {
        nl_cb_put(cb);
        nlmsg_free(msg);
        std::string hello1 = "ERRORRRRRRRRRRRRRRRRRRRRRRRRRRR";
        return env->NewStringUTF(hello1.c_str());
    }

    err = 1;

    nl_cb_err(cb, NL_CB_CUSTOM, error_handler, &err);
    nl_cb_set(cb, NL_CB_FINISH, NL_CB_CUSTOM, finish_handler, &err);
    nl_cb_set(cb, NL_CB_ACK, NL_CB_CUSTOM, ack_handler, &err);

//    while (err > 0)
    nl_recvmsgs(nlstate.nl_sock, cb);

    nl_socket_free(nlstate.nl_sock);

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

int nl80211_init(nl80211_state *state) {
    int err;

    state->nl_sock = nl_socket_alloc();
    if (!state->nl_sock) {
        fprintf(stderr, "Failed to allocate netlink socket.\n");
        return -ENOMEM;
    }

    nl_socket_set_buffer_size(state->nl_sock, 8192, 8192);
    int ret=genl_connect(state->nl_sock);
    if (ret) {
        fprintf(stderr, "Failed to connect to generic netlink.\n");
        err = -ENOLINK;
        goto out_handle_destroy;
    }

    state->nl80211_id = genl_ctrl_resolve(state->nl_sock, "nl80211");
    if (state->nl80211_id < 0) {
        fprintf(stderr, "nl80211 not found.\n");
        err = -ENOENT;
        goto out_handle_destroy;
    }

    return 0;

    out_handle_destroy:
    nl_socket_free(state->nl_sock);
    return err;
}

static int get_survey_info(struct nl_msg *msg, void *arg) {
    struct nlattr *tb[NL80211_ATTR_MAX + 1];
    auto *gnlh = static_cast<genlmsghdr *>(nlmsg_data(nlmsg_hdr(msg)));
    struct nlattr *sinfo[NL80211_SURVEY_INFO_MAX + 1];
    char dev[20];

    static struct nla_policy survey_policy[NL80211_SURVEY_INFO_MAX + 1] = {
            [NL80211_SURVEY_INFO_FREQUENCY] = {.type = NLA_U32},
            [NL80211_SURVEY_INFO_NOISE] = {.type = NLA_U8},
            [NL80211_SURVEY_INFO_CHANNEL_TIME]={.type=NLA_U64},
            [NL80211_SURVEY_INFO_CHANNEL_TIME_BUSY]={.type=NLA_U64},
    };

    nla_parse(tb, NL80211_ATTR_MAX, genlmsg_attrdata(gnlh, 0),
              genlmsg_attrlen(gnlh, 0), NULL);

    if (!tb[NL80211_ATTR_SURVEY_INFO]) {
        fprintf(stderr, "survey data missing!\n");
        return NL_SKIP;
    }

    if (nla_parse_nested(sinfo, NL80211_SURVEY_INFO_MAX,
                         tb[NL80211_ATTR_SURVEY_INFO],
                         survey_policy)) {
        fprintf(stderr, "failed to parse nested attributes!\n");
        return NL_SKIP;
    }

//    uint64_t activeTime, busyTime;
//    if (sinfo[NL80211_SURVEY_INFO_FREQUENCY] && sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME] &&
//        sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_BUSY] && sinfo[NL80211_SURVEY_INFO_NOISE]) {
//        activeTime = nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME]);
//        busyTime = nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_BUSY]);
//
//    }

    if (sinfo[NL80211_SURVEY_INFO_FREQUENCY])
        printf("\tfrequency:\t\t\t%u MHz%s\n",
               nla_get_u32(sinfo[NL80211_SURVEY_INFO_FREQUENCY]),
               sinfo[NL80211_SURVEY_INFO_IN_USE] ? " [in use]" : "");
    if (sinfo[NL80211_SURVEY_INFO_NOISE])
        printf("\tnoise:\t\t\t\t%d dBm\n",
               (int8_t) nla_get_u8(sinfo[NL80211_SURVEY_INFO_NOISE]));
    if (sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME])
        printf("\tchannel active time:\t\t%llu ms\n",
               (unsigned long long) nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME]));
    if (sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_BUSY])
        printf("\tchannel busy time:\t\t%llu ms\n",
               (unsigned long long) nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_BUSY]));
    if (sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_EXT_BUSY])
        printf("\textension channel busy time:\t%llu ms\n",
               (unsigned long long) nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_EXT_BUSY]));
    if (sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_RX])
        printf("\tchannel receive time:\t\t%llu ms\n",
               (unsigned long long) nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_RX]));
    if (sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_TX])
        printf("\tchannel transmit time:\t\t%llu ms\n",
               (unsigned long long) nla_get_u64(sinfo[NL80211_SURVEY_INFO_CHANNEL_TIME_TX]));

    return NL_SKIP;

}

static int handle_survey_dump(struct nl80211_state *state,
                              struct nl_cb *cb,
                              struct nl_msg *msg,
                              int argc, char **argv) {
    nl_cb_set(cb, NL_CB_VALID, NL_CB_CUSTOM, get_survey_info, NULL);
    return 0;
}

static struct cmd cmd_dump_handle_survey_dump_NL80211_CMD_GET_SURVEY_0 __attribute__((used))
        __attribute__((section("__cmd"))) = {
                .name=("dump"),
                .args=(__null),
                .cmd=(NL80211_CMD_GET_SURVEY),
                .nl_msg_flags=(NLM_F_DUMP),
                .hidden=(0), .handler=(handle_survey_dump),
                .help=("List all gathered channel survey data"),
                .parent=&(__section_survey),
                .selector=(__null),};


static int error_handler(struct sockaddr_nl *nla, struct nlmsgerr *err,
                         void *arg) {
    int *ret = static_cast<int *>(arg);
    *ret = err->error;
    return NL_STOP;
}

static int finish_handler(struct nl_msg *msg, void *arg) {
    int *ret = static_cast<int *>(arg);
    *ret = 0;
    return NL_SKIP;
}

static int ack_handler(struct nl_msg *msg, void *arg) {
    int *ret = static_cast<int *>(arg);
    *ret = 0;
    return NL_STOP;
}