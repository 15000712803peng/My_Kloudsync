package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SharedPreferencesUtils;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.tool.SyncWebNoteActionsCache;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.tqltech.tqlpencomm.Dot;
import com.ub.service.KloudWebClientManager;
import com.ub.techexcel.bean.NewBookPagesBean;
import com.ub.techexcel.bean.NoteDotBean;
import com.ub.techexcel.bean.NoteInfoBean;
import com.ub.techexcel.bean.SendWebScoketNoteBean;
import com.ub.techexcel.bean.SyncNoteBean;
import com.ub.techexcel.bean.UploadNoteBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.kloudsync.techexcel.config.AppConfig.UPLOADPENDATA;

public class EverPenDataManger {
	private final String TAG = EverPenDataManger.class.getSimpleName();

	private static EverPenDataManger mManger;
	private EverPenManger mEverPenManger;
	private Activity mActivity;
	private ServiceInterfaceTools mRequsetTools;
	private final double B5_WIDTH = 119.44;
	private final double B5_HEIGHT = 168;
	private final int RIBBONWIDECENTER = 2156;
	private final int RIBBONHEIGHT = 600;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPLOADPENDATA:
					Observable.just("getData").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
						@Override
						public void accept(String s) throws Exception {

							Map<String, NoteDotBean> partWebActions = SyncWebNoteActionsCache.getInstance(App.getAppContext()).getPartWebActions();
							if (partWebActions != null && partWebActions.size() != 0) {
								List<NoteInfoBean.DataBean> noteInfoList = SharedPreferencesUtils.getList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, new TypeToken<List<NoteInfoBean.DataBean>>() {
								});
								//请求noteId相关信息请求参数实体类
								NewBookPagesBean newBookPagesBean = new NewBookPagesBean();
								newBookPagesBean.setPeertimeToken(AppConfig.UserToken);
								List<NewBookPagesBean.BookPagesBean> bookPagesBeans = new ArrayList<>();
								//上传笔数据请求参数实体类
								SyncNoteBean syncNoteBean = new SyncNoteBean();
								syncNoteBean.setPeertimeToken(AppConfig.UserToken);
								List<SyncNoteBean.BookPagesBean> bookPagesList = new ArrayList<>();
								List<SyncNoteBean.DrawingDataBean> drawingDataList = new ArrayList<>();
								List<String> uuidList = new ArrayList<>();

								Date date = null;
								try {
									date = mSimpleDateFormat.parse(" 2010-01-01 00:00:00");
								} catch (ParseException e) {
									e.printStackTrace();
								}
								Iterator<String> iterator = partWebActions.keySet().iterator();
								while (iterator.hasNext()) {
									if (drawingDataList.size() > 4999) {
										break;
									}
									String dotId = iterator.next();
									NoteDotBean noteDotBean = partWebActions.get(dotId);
									String uuid = noteDotBean.getDotId();
									uuidList.add(uuid);
									Dot dot = noteDotBean.getDot();
									String address = dot.OwnerID + "." + dot.SectionID + "." + dot.BookID + "." + dot.PageID;
									int eventType = 0;
									switch (dot.type) {
										case PEN_DOWN:
											eventType = 2;
											break;
										case PEN_MOVE:
											eventType = 1;
											break;
										case PEN_UP:
											//发送数据给web端
											break;
									}
									int force = dot.force * 20;
									if (force == 0) {
										//不处理
									} else if (force < 500) {
										force = 500;
									} else if (force > 1200) {
										force = 1200;
									}
									double dotX = dot.x + Double.valueOf("0." + dot.fx);
									double dotY = dot.y + Double.valueOf("0." + dot.fy);
									int x = (int) (dotX / B5_WIDTH * 5600);
									int y = (int) (dotY / B5_HEIGHT * 7920);

									NoteInfoBean.DataBean newBookPagesDataBean = new NoteInfoBean.DataBean();
									newBookPagesDataBean.setAddress(address);
									if (!noteInfoList.contains(newBookPagesDataBean)) {
										NewBookPagesBean.BookPagesBean pagesBean = new NewBookPagesBean.BookPagesBean();
										pagesBean.setPageAddress(address);
										pagesBean.setPenId(mEverPenManger.getCurrentPen().getMacAddress());
										if (!bookPagesBeans.contains(pagesBean)) {
											bookPagesBeans.add(pagesBean);
										}
									}

									for (NoteInfoBean.DataBean noteInfoDataBean : noteInfoList) {
										if (noteInfoDataBean.getAddress().equals(address)) {
											SyncNoteBean.BookPagesBean syncNoteBookPagesBean = new SyncNoteBean.BookPagesBean();
											syncNoteBookPagesBean.setNoteId(noteInfoDataBean.getNoteId());
											syncNoteBookPagesBean.setFileId(noteInfoDataBean.getFileId());
											syncNoteBookPagesBean.setTargetFolderKey(noteInfoDataBean.getTargetFolder());
											syncNoteBookPagesBean.setPageAddress(address);
											if (!bookPagesList.contains(syncNoteBookPagesBean)) {
												bookPagesList.add(syncNoteBookPagesBean);
											}
										}
									}


									SyncNoteBean.DrawingDataBean drawingDataBean = new SyncNoteBean.DrawingDataBean();
									drawingDataBean.setAddress(address);
									drawingDataBean.setUserID(AppConfig.UserID);
									drawingDataBean.setEvent_type(eventType);
									drawingDataBean.setForce(force);
									drawingDataBean.setPoint_x(String.valueOf(x));
									drawingDataBean.setPoint_y(String.valueOf(y));
									long timelong = date.getTime() + dot.timelong;
									BigDecimal bigDecimal = new BigDecimal(String.valueOf(timelong));
									BigDecimal bigDecimal2 = new BigDecimal("1000");
									bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
									bigDecimal2.setScale(3, BigDecimal.ROUND_HALF_UP);
									String time = bigDecimal.divide(bigDecimal2).toString();
									drawingDataBean.setTimestamp(time);
									drawingDataBean.setPenID(mEverPenManger.getCurrentPen().getMacAddress());
									if (eventType == 2) {
										drawingDataBean.setStrokeID(uuid);
									}
									drawingDataList.add(drawingDataBean);
								}
								syncNoteBean.setBookPages(bookPagesList);
								syncNoteBean.setDrawingData(drawingDataList);
								newBookPagesBean.setBookPages(bookPagesBeans);
								Log.e(TAG, TAG + "handleMessage_UPLOADPENDATA_noteInfoList" + noteInfoList.size() + "_syncNoteBean.setBookPages = " + bookPagesList.size()
										+ "_syncNoteBean.setDrawingData=" + drawingDataList.size() + "_newBookPagesBean.setBookPages=" + bookPagesBeans.size());

							/*	post(new Runnable() {
									@Override
									public void run() {*/
								if (newBookPagesBean.getBookPages().size() > 0) {
									requestNewBookPages(syncNoteBean, newBookPagesBean, uuidList, null, false, false, null);
								} else {
									uploadDrawing(syncNoteBean, uuidList);
								}
									/*}
								});*/
							} else {
								sendEmptyMessageDelayed(UPLOADPENDATA, 5000);
							}
						}
					}).subscribe();
					break;
			}
		}
	};

	public EverPenDataManger(EverPenManger everPenManger, Activity activity) {
		mActivity = activity;
		mEverPenManger = everPenManger;
		mRequsetTools = ServiceInterfaceTools.getinstance();
	}

	public static EverPenDataManger getInstace(EverPenManger everPenManger, Activity activity) {
		if (mManger == null) {
			synchronized (EverPenDataManger.class) {
				if (mManger == null) {
					mManger = new EverPenDataManger(everPenManger, activity);
				}
			}
		}
		return mManger;
	}

	public void sendHandlerMessage() {
		mHandler.sendEmptyMessageDelayed(UPLOADPENDATA, 5000);
	}

	public void removeHandlerMessage() {
		mHandler.removeMessages(UPLOADPENDATA);
	}

	public void removeCallbacksAndMessages(Object token) {
		mHandler.removeCallbacksAndMessages(token);
	}

	public void cacheDotListData(List<NoteDotBean> noteDotBeans) {
		SyncWebNoteActionsCache.getInstance(App.getAppContext()).cacheMapActions(noteDotBeans);
	}

	public void sendNoteDataToWebSocket(final Dot dot, final List<NoteDotBean> dotList) {
		int noteId = 0;
		long downTimeLong = 0;
		long upTimeLong;
		String uuid = null;
		boolean isSendOpenOrCloseMsg = false;
		boolean isSendOCRMsg = false;
		final List<NoteInfoBean.DataBean> noteInfoList = SharedPreferencesUtils.getList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, new TypeToken<List<NoteInfoBean.DataBean>>() {
		});

		NewBookPagesBean newBookPagesBean = null;
		List<NewBookPagesBean.BookPagesBean> bookPagesBeans = null;
		SendWebScoketNoteBean webScoketDataBean = new SendWebScoketNoteBean();
		String address = dot.OwnerID + "." + dot.SectionID + "." + dot.BookID + "." + dot.PageID;
		NoteInfoBean.DataBean newPagesDataBean = new NoteInfoBean.DataBean();
		newPagesDataBean.setAddress(address);
		if (!noteInfoList.contains(newPagesDataBean)) {
			newBookPagesBean = new NewBookPagesBean();
			newBookPagesBean.setPeertimeToken(AppConfig.UserToken);
			bookPagesBeans = new ArrayList<>();
			NewBookPagesBean.BookPagesBean pagesBean = new NewBookPagesBean.BookPagesBean();
			pagesBean.setPageAddress(address);
			pagesBean.setPenId(mEverPenManger.getCurrentPen().getMacAddress());
			if (!bookPagesBeans.contains(pagesBean)) {
				bookPagesBeans.add(pagesBean);
			}
			newBookPagesBean.setBookPages(bookPagesBeans);
		} else {
			for (int i = 0; i < noteInfoList.size(); i++) {
				if (noteInfoList.get(i).getAddress().equals(address)) {
					noteId = noteInfoList.get(i).getNoteId();
					break;
				}
			}
		}

		Date date = null;
		try {
			date = mSimpleDateFormat.parse(" 2010-01-01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		webScoketDataBean.setAddress(address);
		List<SendWebScoketNoteBean.LinesBean> linesBeans = new ArrayList<>();

		for (NoteDotBean noteDotBean : dotList) {
			Dot bean = noteDotBean.getDot();
			int eventType = 0;
			switch (bean.type) {
				case PEN_DOWN:
					uuid = noteDotBean.getDotId();
					downTimeLong = bean.timelong;
					eventType = 2;
					break;
				case PEN_UP:
					upTimeLong = bean.timelong;
					long differenceTime = upTimeLong - downTimeLong;
					if (differenceTime < 800) {//只是点一个点的话笔的timelong时间不精确,正常时间过了两秒,笔的按下和抬起的timelong差值才个位数
						double dotX = bean.x + Double.valueOf("0." + bean.fx);
						double dotY = bean.y + Double.valueOf("0." + bean.fy);
						int x = (int) (dotX / B5_WIDTH * 5600);
						int y = (int) (dotY / B5_HEIGHT * 7920);
						if (y < RIBBONHEIGHT) {
							if (x < RIBBONWIDECENTER && !isSendOCRMsg && !isSendOpenOrCloseMsg) {
								isSendOCRMsg = true;
							} else if (x > RIBBONWIDECENTER && !isSendOpenOrCloseMsg && !isSendOCRMsg) {
								isSendOpenOrCloseMsg = true;
							}
						}
					}
					break;
			}
			if (eventType == 2) {
				SendWebScoketNoteBean.LinesBean linesBean = new SendWebScoketNoteBean.LinesBean();
				List<List<String>> pointList = new ArrayList<>();

				linesBean.setId(noteDotBean.getDotId());
				for (NoteDotBean dotbean2 : dotList) {
					Dot pointDot = dotbean2.getDot();
					int force = bean.force * 20;
					if (force == 0) {
						//不处理
					} else if (force < 500) {
						force = 500;
					} else if (force > 1200) {
						force = 1200;
					}
					double dotX = pointDot.x + Double.valueOf("0." + pointDot.fx);
					double dotY = pointDot.y + Double.valueOf("0." + pointDot.fy);
					int x = (int) (dotX / B5_WIDTH * 5600);
					int y = (int) (dotY / B5_HEIGHT * 7920);
					long timelong = date.getTime() + pointDot.timelong;
					BigDecimal bigDecimal = new BigDecimal(String.valueOf(timelong));
					BigDecimal bigDecimal2 = new BigDecimal("1000");
					bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
					bigDecimal2.setScale(3, BigDecimal.ROUND_HALF_UP);
					String time = bigDecimal.divide(bigDecimal2).toString();

					List<String> pointDataList = new ArrayList<>();
					pointDataList.add(String.valueOf(x));
					pointDataList.add(String.valueOf(y));
					pointDataList.add(String.valueOf(force));
					pointDataList.add(time);
					pointList.add(pointDataList);
				}
				linesBean.setPoints(pointList);
				linesBeans.add(linesBean);
			}

		}
		webScoketDataBean.setLines(linesBeans);
		webScoketDataBean.setWidth(5600);
		webScoketDataBean.setHeight(7920);
		webScoketDataBean.setPaper("A4");
		Log.e(TAG, "sendNoteDataToWebSocket : noteInfoList = " + noteInfoList.size() + "address = " + address + "noteId = "
				+ noteId + "webScoketDataBean.getLines() = " + webScoketDataBean.getLines().size());
		/*mHandler.post(new Runnable() {
			@Override
			public void run() {*/
		if (bookPagesBeans != null && bookPagesBeans.size() > 0) {
			requestNewBookPages(null, newBookPagesBean, null, webScoketDataBean, isSendOCRMsg, isSendOpenOrCloseMsg, uuid);
		} else {
			if (!isSendOCRMsg && !isSendOpenOrCloseMsg) {
				if (KloudWebClientManager.getInstance() != null) {
					SocketMessageManager.getManager(App.getAppContext()).sendMessage_myNoteData(address, noteId, webScoketDataBean);
				}
			} else {
				if (isSendOCRMsg) {
					//发送OCR消息
				} else if (isSendOpenOrCloseMsg) {
					//发送OPENORCLOSE消息
					if (KloudWebClientManager.getInstance() != null) {
						SocketMessageManager.getManager(App.getAppContext()).sendMessage_openOrCloseNote(address, noteId, uuid);
					}
				}
			}
		}

			/*}
		});*/
	}

	public synchronized void requestNewBookPages(final SyncNoteBean syncNoteBean, final NewBookPagesBean newBookPagesBean, final List<String> uuidList,
	                                             final SendWebScoketNoteBean webScoketDataBean, boolean isSendOCRMsg, boolean isSendOpenOrCloseMsg, String uuid) {
//		Log.e(TAG, Thread.currentThread().toString());
		/*Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, NoteInfoBean>() {
			@Override
			public NoteInfoBean apply(String s) throws Exception {*/
		String path = AppConfig.URL_LIVEDOC + "newBookPages";
		/*return */
		final NoteInfoBean noteinfobean = mRequsetTools.requestNewBookPages(path, newBookPagesBean.getPeertimeToken(), newBookPagesBean.getBookPages());
			/*}
		}).doOnNext(new Consumer<NoteInfoBean>() {
			@Override
			public void accept(final NoteInfoBean noteinfobean) throws Exception {*/
		if (noteinfobean != null) {
			if (noteinfobean.isSuccess()) {
				List<NoteInfoBean.DataBean> dataBeanList = noteinfobean.getData();
				for (int i = 0; i < dataBeanList.size(); i++) {
					if (dataBeanList.get(i).isSuccess()) {
						Log.e(TAG, Thread.currentThread().toString() + dataBeanList.get(i).getNoteId());
					} else {
						Log.e(TAG, Thread.currentThread().toString() + dataBeanList.get(i).getErrMsg());
					}
				}
				if (webScoketDataBean == null) {
					for (NoteInfoBean.DataBean bean : dataBeanList) {
						SyncNoteBean.BookPagesBean bookPagesBean = new SyncNoteBean.BookPagesBean();
						bookPagesBean.setNoteId(bean.getNoteId());
						bookPagesBean.setFileId(bean.getFileId());
						bookPagesBean.setTargetFolderKey(bean.getTargetFolder());
						bookPagesBean.setPageAddress(bean.getAddress());
						syncNoteBean.getBookPages().add(bookPagesBean);
						Log.e(TAG, "requestNewBookPages : dataBeanList = " + dataBeanList.size() + "noteid = " + bean.getNoteId());
					}
					SharedPreferencesUtils.putPenInfoList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, dataBeanList, new TypeToken<List<NoteInfoBean.DataBean>>() {
					});
					/*mHandler.post(new Runnable() {
						@Override
						public void run() {*/
					uploadDrawing(syncNoteBean, uuidList);
						/*}
					});*/
				} else {
					SharedPreferencesUtils.putPenInfoList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, dataBeanList, new TypeToken<List<NoteInfoBean.DataBean>>() {
					});
					for (NoteInfoBean.DataBean bean : dataBeanList) {
						if (!isSendOCRMsg && !isSendOpenOrCloseMsg) {
							//发送笔记数据消息
							if (KloudWebClientManager.getInstance() != null) {
								SocketMessageManager.getManager(App.getAppContext()).sendMessage_myNoteData(webScoketDataBean.getAddress(), bean.getNoteId(), webScoketDataBean);
							}
						} else {
							if (isSendOCRMsg) {
								//发送OCR消息
							} else if (isSendOpenOrCloseMsg) {
								//发送OPENORCLOSE消息
								if (KloudWebClientManager.getInstance() != null) {
									SocketMessageManager.getManager(App.getAppContext()).sendMessage_openOrCloseNote(webScoketDataBean.getAddress(), bean.getNoteId(), uuid);
								}
							}
						}
						Log.e(TAG, "requestNewBookPagesSocket : dataBeanList = " + dataBeanList.size() + "address = " + webScoketDataBean.getAddress()
								+ "noteId = " + bean.getNoteId() + "webScoketDataBean.getLines() = " + webScoketDataBean.getLines().size());
					}
				}
			} else {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						NoteInfoBean.ErrorBean error = noteinfobean.getError();
						if (error != null) {
							String errorMessage = error.getErrorMessage();
							ToastUtils.show(mActivity, errorMessage);
						}
					}
				});
				sendHandlerMessage();
			}
		} else {
			sendHandlerMessage();
		}

			/*}
		}).subscribe();*/
	}

	public synchronized void uploadDrawing(final SyncNoteBean syncNoteBean, final List<String> uuidList) {
		Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, UploadNoteBean>() {
			@Override
			public UploadNoteBean apply(String s) throws Exception {
				String path = AppConfig.URL_LIVEDOC + "uploadDrawing";
				return mRequsetTools.uploadDrawing(path, syncNoteBean.getPeertimeToken(), syncNoteBean.getBookPages(), syncNoteBean.getDrawingData());
			}
		}).doOnNext(new Consumer<UploadNoteBean>() {
			@Override
			public void accept(final UploadNoteBean bean) throws Exception {
				if (bean != null) {
					if (bean.isSuccess()) {
						SyncWebNoteActionsCache.getInstance(App.getAppContext()).removeListActions(uuidList);
						sendHandlerMessage();
					} else {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								UploadNoteBean.ErrorBean error = bean.getError();
								String errorMessage = error.getErrorMessage();
								ToastUtils.show(mActivity, errorMessage);
							}
						});
						sendHandlerMessage();
					}
				} else {
					sendHandlerMessage();
				}
			}
		}).subscribe();
	}
}
