/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.jprotobuf.pbrpc.transport.handler;

/**
 * Error code list.
 *
 * @author xiemalin
 * @since 1.0
 */
public class ErrorCodes {

    /** success status. */
    public static final int ST_SUCCESS = 0;

    /** 未知异常. */
    public static final int ST_ERROR = 2001;

    /** 方法未找到异常. */
    public static final int ST_SERVICE_NOTFOUND = 1001;

    /** 方法未找到异常. */
    public static final int ST_METHOD_NOTFOUND = 1002;

    /** 压缩与解压异常. */
    public static final int ST_ERROR_COMPRESS = 3000;

    /** service not found. */
    public static final String MSG_SERVICE_NOTFOUND = "service not found";

    /** read time out. */
    public static final int ST_READ_TIMEOUT = 62;

    /** onceTalkTimeout timeout message. */
    public static final String MSG_READ_TIMEOUT =
            "method request time out, please check 'onceTalkTimeout' property. current value is:";

    /**
     * check is error code is equals to ST_SUCCESS.
     *
     * @param errorCode the error code
     * @return true, if is success
     */
    public static boolean isSuccess(int errorCode) {
        return ST_SUCCESS == errorCode;
    }

    /*
     * SYS_EPERM = 1; // Operation not permitted SYS_ENOENT = 2; // No such file or directory SYS_ESRCH = 3; // No such
     * process SYS_EINTR = 4; // Interrupted system call SYS_EIO = 5; // I/O error SYS_ENXIO = 6; // No such device or
     * address SYS_E2BIG = 7; // Arg list too long SYS_ENOEXEC = 8; // Exec format error SYS_EBADF = 9; // Bad file
     * number SYS_ECHILD = 10; // No child processes SYS_EAGAIN = 11; // Try again SYS_ENOMEM = 12; // Out of memory
     * SYS_EACCES = 13; // Permission denied SYS_EFAULT = 14; // Bad address SYS_ENOTBLK = 15; // Block device required
     * SYS_EBUSY = 16; // Device or resource busy SYS_EEXIST = 17; // File exists SYS_EXDEV = 18; // Cross-device link
     * SYS_ENODEV = 19; // No such device SYS_ENOTDIR = 20; // Not a directory SYS_EISDIR = 21; // Is a directory
     * SYS_EINVAL = 22; // Invalid argument SYS_ENFILE = 23; // File table overflow SYS_EMFILE = 24; // Too many open
     * files SYS_ENOTTY = 25; // Not a typewriter SYS_ETXTBSY = 26; // Text file busy SYS_EFBIG = 27; // File too large
     * SYS_ENOSPC = 28; // No space left on device SYS_ESPIPE = 29; // Illegal seek SYS_EROFS = 30; // Read-only file
     * system SYS_EMLINK = 31; // Too many links SYS_EPIPE = 32; // Broken pipe SYS_EDOM = 33; // Math argument out of
     * domain of func SYS_ERANGE = 34; // Math result not representable SYS_EDEADLK = 35; // Resource deadlock would
     * occur SYS_ENAMETOOLONG = 36; // File name too long SYS_ENOLCK = 37; // No record locks available SYS_ENOSYS = 38;
     * // Function not implemented SYS_ENOTEMPTY = 39; // Directory not empty SYS_ELOOP = 40; // Too many symbolic links
     * encountered SYS_ENOMSG = 42; // No message of desired type SYS_EIDRM = 43; // Identifier removed SYS_ECHRNG = 44;
     * // Channel number out of range SYS_EL2NSYNC = 45; // Level= 2;not synchronized SYS_EL3HLT = 46; // Level=
     * 3;halted SYS_EL3RST = 47; // Level= 3;reset SYS_ELNRNG = 48; // Link number out of range SYS_EUNATCH = 49; //
     * Protocol driver not attached SYS_ENOCSI = 50; // No CSI structure available SYS_EL2HLT = 51; // Level= 2;halted
     * SYS_EBADE = 52; // Invalid exchange SYS_EBADR = 53; // Invalid request descriptor SYS_EXFULL = 54; // Exchange
     * full SYS_ENOANO = 55; // No anode SYS_EBADRQC = 56; // Invalid request code SYS_EBADSLT = 57; // Invalid slot
     * SYS_EBFONT = 59; // Bad font file format SYS_ENOSTR = 60; // Device not a stream SYS_ENODATA = 61; // No data
     * available SYS_ETIME = 62; // Timer expired SYS_ENOSR = 63; // Out of streams resources SYS_ENONET = 64; //
     * Machine is not on the network SYS_ENOPKG = 65; // Package not installed SYS_EREMOTE = 66; // Object is remote
     * SYS_ENOLINK = 67; // Link has been severed SYS_EADV = 68; // Advertise error SYS_ESRMNT = 69; // Srmount error
     * SYS_ECOMM = 70; // Communication error on send SYS_EPROTO = 71; // Protocol error SYS_EMULTIHOP = 72; // Multihop
     * attempted SYS_EDOTDOT = 73; // RFS specific error SYS_EBADMSG = 74; // Not a data message SYS_EOVERFLOW = 75; //
     * Value too large for defined data type SYS_ENOTUNIQ = 76; // Name not unique on network SYS_EBADFD = 77; // File
     * descriptor in bad state SYS_EREMCHG = 78; // Remote address changed SYS_ELIBACC = 79; // Can not access a needed
     * shared library SYS_ELIBBAD = 80; // Accessing a corrupted shared library SYS_ELIBSCN = 81; // .lib section in
     * a.out corrupted SYS_ELIBMAX = 82; // Attempting to link in too many shared libraries SYS_ELIBEXEC = 83; // Cannot
     * exec a shared library directly SYS_EILSEQ = 84; // Illegal byte sequence SYS_ERESTART = 85; // Interrupted system
     * call should be restarted SYS_ESTRPIPE = 86; // Streams pipe error SYS_EUSERS = 87; // Too many users SYS_ENOTSOCK
     * = 88; // Socket operation on non-socket SYS_EDESTADDRREQ = 89; // Destination address required SYS_EMSGSIZE = 90;
     * // Message too long SYS_EPROTOTYPE = 91; // Protocol wrong type for socket SYS_ENOPROTOOPT = 92; // Protocol not
     * available SYS_EPROTONOSUPPORT = 93; // Protocol not supported SYS_ESOCKTNOSUPPORT = 94; // Socket type not
     * supported SYS_EOPNOTSUPP = 95; // Operation not supported on transport endpoint SYS_EPFNOSUPPORT = 96; //
     * Protocol family not supported SYS_EAFNOSUPPORT = 97; // Address family not supported by protocol SYS_EADDRINUSE =
     * 98; // Address already in use SYS_EADDRNOTAVAIL = 99; // Cannot assign requested address SYS_ENETDOWN = 100; //
     * Network is down SYS_ENETUNREACH = 101; // Network is unreachable SYS_ENETRESET = 102; // Network dropped
     * connection because of reset SYS_ECONNABORTED = 103; // Software caused connection abort SYS_ECONNRESET = 104; //
     * Connection reset by peer SYS_ENOBUFS = 105; // No buffer space available SYS_EISCONN = 106; // Transport endpoint
     * is already connected SYS_ENOTCONN = 107; // Transport endpoint is not connected SYS_ESHUTDOWN = 108; // Cannot
     * send after transport endpoint shutdown SYS_ETOOMANYREFS = 109; // Too many references: cannot splice
     * SYS_ETIMEDOUT = 110; // Connection timed out SYS_ECONNREFUSED = 111; // Connection refused SYS_EHOSTDOWN = 112;
     * // Host is down SYS_EHOSTUNREACH = 113; // No route to host SYS_EALREADY = 114; // Operation already in progress
     * SYS_EINPROGRESS = 115; // Operation now in progress SYS_ESTALE = 116; // Stale NFS file handle SYS_EUCLEAN = 117;
     * // Structure needs cleaning SYS_ENOTNAM = 118; // Not a XENIX named type file SYS_ENAVAIL = 119; // No XENIX
     * semaphores available SYS_EISNAM = 120; // Is a named type file SYS_EREMOTEIO = 121; // Remote I/O error
     * SYS_EDQUOT = 122; // Quota exceeded SYS_ENOMEDIUM = 123; // No medium found SYS_EMEDIUMTYPE = 124; // Wrong
     * medium type SYS_ECANCELED = 125; // Operation Cancelled SYS_ENOKEY = 126; // Required key not available
     * SYS_EKEYEXPIRED = 127; // Key has expired SYS_EKEYREVOKED = 128; // Key has been revoked SYS_EKEYREJECTED = 129;
     * // Key was rejected by service
     * 
     * // Errno caused by client ENOSERVICE = 1001; // Service not found ENOMETHOD = 1002; // Method not found EREQUEST
     * = 1003; // Bad Request EAUTH = 1004; // Unauthorized
     * 
     * // Errno caused by server EINTERNAL = 2001; // Internal Server Error ELOGOFF = 2002; // Server is logging off
     * ERESPONSE = 2003; // Bad Response
     */
}
