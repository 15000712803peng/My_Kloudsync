package com.kloudsync.techexcel.mvp.view;

public interface CurrentPenStatusView extends KloudView {

	void setCurrentPenStatus(int resStringId, int resImgId, int resBtnStringId);

	void setPenStatusTextColor(int resColorId);

	void getPenPower();

	void getPenMemory();

	void setCurrentPenTips(boolean isShow);

	void setCurrentPenBattery(int batteryLength, boolean bIsCharging);

	void setCurrentPenStorage(int storageLength);

	void setPenName(boolean bIsSuccess);
}
