package org.floracore.api.commands.report;

/**
 * 举报当前的状态类型
 */
public enum ReportStatus {
	/**
	 * 等待中
	 */
	WAITING,
	/**
	 * 已受理,等待处理
	 */
	ACCEPTED,
	/**
	 * 已处理
	 */
	ENDED
}
