package com.kloudsync.techexcel.help;

import com.kloudsync.techexcel.bean.EverPen;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;

public interface MyTQLPenSignal {

	void getBleManager(PenCommAgent bleManager);
	/**扫描蓝牙设备回调start*/
	/**
	 * 扫描结果
	 */
	void onScanResult(EverPen everPen);

	/**
	 * 扫描失败
	 *
	 * @param e
	 */
	void onScanFailed(BLEException e);

	/**扫描蓝牙设备回调end*/

	/**连接笔回调start*/
	/**
	 * 连接成功回调
	 */
	void onConnected();

	/**
	 * 断开连接回调
	 */
	void onDisconnected();

	/**
	 * 连接失败回调
	 */
	void onConnectFailed();

	/**连接笔回调end*/

	/**笔数据回调start*/
	/**
	 * 笔实时数据回调
	 *
	 * @param dot
	 */
	void onReceiveDot(Dot dot);

	/**
	 * 笔离线数据回调
	 *
	 * @param dot
	 */
	void onReceiveOfflineStrokes(Dot dot);

	/**笔数据回调end*/

	/**
	 * 获取笔的电量信息回调函数
	 *
	 * @param penBattery
	 * @param bIsCharging
	 */
	void onReceivePenBattery(int penBattery, boolean bIsCharging);

	/**
	 * 获取笔已使用内存回调函数
	 *
	 * @param penMemory
	 */
	void onReceivePenMemory(int penMemory);

	/**对应错误码回调start*/
	/**
	 * 蓝牙连接超时
	 */
	void bleConnectTimeout();

	/**
	 * 设置笔名超时
	 */
	void setNameTimeout();

	/**
	 * 获取电量超时
	 */
	void requestBattaryTimeout();

	/**
	 * 获取已使用内存超时
	 * 获取笔的已用内存,命令下发失败
	 */
	void requestMemoryTimeout();

	/**
	 * 设置笔名回 调函数
	 *
	 * @param bIsSuccess
	 */
	void onPenNameSetupResponse(boolean bIsSuccess);


	/**对应错误码回调end*/

}
