package org.concord.sensor.labquest.jna;

@SuppressWarnings("serial")
public class LabQuestCommandException extends LabQuestException {

	private byte lastCmd;
	private byte lastCmdStatus;
	private byte lastCmdWithErrorRespSentOvertheWire;
	private byte lastErrorSentOvertheWire;

	public LabQuestCommandException(byte cmd, byte status) {
		this.lastCmd = cmd;
		this.lastCmdStatus = status;
	}

	public LabQuestCommandException(byte lastCmd, byte lastCmdStatus,
			byte lastCmdWithErrorRespSentOvertheWire, byte lastErrorSentOvertheWire){
		this.lastCmd = lastCmd;
		this.lastCmdStatus = lastCmdStatus;
		this.lastCmdWithErrorRespSentOvertheWire = lastCmdWithErrorRespSentOvertheWire;
		this.lastErrorSentOvertheWire = lastErrorSentOvertheWire;
	}
	
	public String getMessage() {
		return String.format("lastCmd: %#04x lastCmdStatus: %s(%#04x) " +
                "lastCmdWithErrorRespSentOvertheWire: %#04x " +
		        "lastErrorSentOvertheWire: %s(%#04x)",
		  		lastCmd, getStatusString(lastCmdStatus), lastCmdStatus,
		  		lastCmdWithErrorRespSentOvertheWire,
		  		getStatusString(lastErrorSentOvertheWire), lastErrorSentOvertheWire);
	}
	
	@Override
	public boolean isCommunicationError() {
		return lastCmdStatus == NGIOSourceCmds.STATUS_ERROR_COMMUNICATION;
	}
	
	public static String getStatusString(byte status){
		switch(status) {
			case NGIOSourceCmds.STATUS_SUCCESS:
				return "success";
			case NGIOSourceCmds.STATUS_NOT_READY_FOR_NEW_CMD:
				return "not ready for new command";
			case NGIOSourceCmds.STATUS_CMD_NOT_SUPPORTED:
				return "command not supported";
			case NGIOSourceCmds.STATUS_INTERNAL_ERROR1:
				return "internal error 1";
			case NGIOSourceCmds.STATUS_INTERNAL_ERROR2:
				return "internal error 2";
			case NGIOSourceCmds.STATUS_ERROR_CANNOT_CHANGE_PERIOD_WHILE_COLLECTING:
				return "cannot change period while collecting";
			case NGIOSourceCmds.STATUS_ERROR_CANNOT_READ_NV_MEM_BLK_WHILE_COLLECTING_FAST:
				return "cannot read NV memory bulk while collecting";
			case NGIOSourceCmds.STATUS_ERROR_INVALID_PARAMETER:
				return "invalid parameter";
			case NGIOSourceCmds.STATUS_ERROR_CANNOT_WRITE_FLASH_WHILE_COLLECTING:
				return "cannot write flash while collecting";
			case NGIOSourceCmds.STATUS_ERROR_CANNOT_WRITE_FLASH_WHILE_HOST_FIFO_BUSY:
				return "cannot write flash while host fifo busy";
			case NGIOSourceCmds.STATUS_ERROR_OP_BLOCKED_WHILE_COLLECTING:
				return "operation blocked while collecting";
			case NGIOSourceCmds.STATUS_ERROR_CALCULATOR_CANNOT_MEASURE_WITH_NO_BATTERIES:
				return "calculator cannot measure with no batteries";
			case NGIOSourceCmds.STATUS_ERROR_OP_NOT_SUPPORTED_IN_CURRENT_MODE:
				return "operation not supported in current mode";
			case NGIOSourceCmds.STATUS_ERROR_AUDIO_CONTROL_FAILURE:
				return "audio control failure";
			case NGIOSourceCmds.STATUS_ERROR_AUDIO_STREAM_FAILURE:
				return "audio stream failure";				
			case NGIOSourceCmds.STATUS_ERROR_CANNOT_REALLOCATE_FRAME_BUFFERS:
				return "cannot reallocate frame buffers";
			case NGIOSourceCmds.STATUS_ERROR_COMMUNICATION:
				return "communication error";
			case NGIOSourceCmds.STATUS_INTERNAL_ERROR3:
				return "interal error 3";
			case NGIOSourceCmds.STATUS_INTERNAL_ERROR4:
				return "internal error 4";
			default:
				return "unknown error status";
		}
	}
}
