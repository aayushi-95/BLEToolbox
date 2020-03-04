/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.utility;

public class OtapStatusTag {
    public static final byte gOtapStatusSuccess_c                        = 0x00; /*!< The operation was successful. */
    public static final byte gOtapStatusImageDataNotExpected_c           = 0x01; /*!< The OTAP Server tried to send an image data chunk to the OTAP Client but the Client was not expecting it. */
    public static final byte gOtapStatusUnexpectedTransferMethod_c       = 0x02; /*!< The OTAP Server tried to send an image data chunk using a transfer method the OTAP Client does not support/expect. */
    public static final byte gOtapStatusUnexpectedCmdOnDataChannel_c     = 0x03; /*!< The OTAP Server tried to send an unexpected command (different from a data chunk) on a data Channel (ATT or CoC) */
    public static final byte gOtapStatusUnexpectedL2capChannelOrPsm_c    = 0x04; /*!< The selected channel or PSM is not valid for the selected transfer method (ATT or CoC). */
    public static final byte gOtapStatusUnexpectedOtapPeer_c             = 0x05; /*!< A command was received from an unexpected OTAP Server or Client device. */
    public static final byte gOtapStatusUnexpectedCommand_c              = 0x06; /*!< The command sent from the OTAP peer device is not expected in the current state. */
    public static final byte gOtapStatusUnknownCommand_c                 = 0x07; /*!< The command sent from the OTAP peer device is not known. */
    public static final byte gOtapStatusInvalidCommandLength_c           = 0x08; /*!< Invalid command length. */
    public static final byte gOtapStatusInvalidCommandParameter_c        = 0x09; /*!< A parameter of the command was not valid. */
    public static final byte gOtapStatusFailedImageIntegrityCheck_c      = 0x0A; /*!< The image integrity check has failed. */
    public static final byte gOtapStatusUnexpectedSequenceNumber_c       = 0x0B; /*!< A chunk with an unexpected sequence number has been received. */
    public static final byte gOtapStatusImageSizeTooLarge_c              = 0x0C; /*!< The upgrade image size is too large for the OTAP Client. */
    public static final byte gOtapStatusUnexpectedDataLength_c           = 0x0D; /*!< The length of a Data Chunk was not expected. */
    public static final byte gOtapStatusUnknownFileIdentifier_c          = 0x0E; /*!< The image file identifier is not recognized. */
    public static final byte gOtapStatusUnknownHeaderVersion_c           = 0x0F; /*!< The image file header version is not recognized. */
    public static final byte gOtapStatusUnexpectedHeaderLength_c         = 0x10; /*!< The image file header length is not expected for the current header version. */
    public static final byte gOtapStatusUnexpectedHeaderFieldControl_c   = 0x11; /*!< The image file header field control is not expected for the current header version. */
    public static final byte gOtapStatusUnknownCompanyId_c               = 0x12; /*!< The image file header company identifier is not recognized. */
    public static final byte gOtapStatusUnexpectedImageId_c              = 0x13; /*!< The image file header image identifier is not as expected. */
    public static final byte gOtapStatusUnexpectedImageVersion_c         = 0x14; /*!< The image file header image version is not as expected. */
    public static final byte gOtapStatusUnexpectedImageFileSize_c        = 0x15; /*!< The image file header image file size is not as expected. */
    public static final byte gOtapStatusInvalidSubElementLength_c        = 0x16; /*!< One of the sub-elements has an invalid length. */
    public static final byte gOtapStatusImageStorageError_c              = 0x17; /*!< An image storage error has occurred. */
    public static final byte gOtapStatusInvalidImageCrc_c                = 0x18; /*!< The computed CRC does not match the received CRC. */
    public static final byte gOtapStatusInvalidImageFileSize_c           = 0x19; /*!< The image file size is not valid. */
    public static final byte gOtapStatusInvalidL2capPsm_c                = 0x1A; /*!< A block transfer request has been made via the L2CAP CoC method but the specified Psm is not known. */
    public static final byte gOtapStatusNoL2capPsmConnection_c           = 0x1B; /*!< A block transfer request has been made via the L2CAP CoC method but there is no valid PSM connection. */
}
