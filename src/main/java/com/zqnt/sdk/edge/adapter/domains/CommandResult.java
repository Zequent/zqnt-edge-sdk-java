package com.zqnt.sdk.edge.adapter.domains;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommandResult {

	private boolean success;
	private String message;
	private String tid;
	private String sn;
	private CommandResultType resultType;

	/**
	 * Result type enum
	 */
	public enum CommandResultType {
		SUCCESS,
		ERROR,
		NOT_IMPLEMENTED
	}

	/**
	 * Create a success result
	 */
	public static CommandResult success(String message, String sn) {
		return new CommandResult(true, message, null,sn, CommandResultType.SUCCESS);
	}

	/**
	 * Create a success result with transaction ID
	 */
	public static CommandResult success(String message, String transactionId, String sn) {
		return new CommandResult(true, message, transactionId,sn, CommandResultType.SUCCESS);
	}

	/**
	 * Create an error result
	 */
	public static CommandResult error(String message, String sn) {
		return new CommandResult(false, message, null, sn, CommandResultType.ERROR);
	}

	/**
	 * Create an error result with transaction ID
	 */
	public static CommandResult error(String message, String transactionId, String sn) {
		return new CommandResult(false, message, transactionId, sn, CommandResultType.ERROR);
	}

	/**
	 * Create a "not implemented" result
	 * Used for default implementations when customer hasn't implemented a method
	 */
	public static CommandResult notImplemented(String message, String sn) {
		return new CommandResult(false, message, null, sn, CommandResultType.NOT_IMPLEMENTED);
	}

	/**
	 * Check if this is a not-implemented result
	 */
	public boolean isNotImplemented() {
		return resultType == CommandResultType.NOT_IMPLEMENTED;
	}

}
