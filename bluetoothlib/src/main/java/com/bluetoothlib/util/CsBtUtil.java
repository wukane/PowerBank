package com.bluetoothlib.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tool.util.LogUtil;
import com.vfuchong.btlib.model.bt.BtGattAttr;
import com.vfuchong.btlib.model.bt.CsDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 蓝牙工具包，芯海蓝牙芯片的控制集合
 */

public class CsBtUtil {

    // =====================================定义常量=======================================
    // 打印相关的常变量
    /**
     * 标识 *
     */
    private static final String TAG = "CsBtUtil";

    /**
     * 表中元素列表为公司蓝牙芯片显示的名字 *
     */
    public interface VFUCHONG {
        /**
         * 蓝牙核心名字 *
         */
//		public static final String BT_NAME = "O-Band";
        public static final String BT_NAME = "V-Card";
//		public static final String BT_NAME="V-Card1";
//		public static final String BT_NAME="Chipsea-BLE";
    }

    /**
     * uuid值-心跳检测 *
     */
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString(BtGattAttr.HEART_RATE_MEASUREMENT);

    /**
     * uuid值-输入 *
     */
    public final static UUID UUID_ISSC_RX = UUID
            .fromString(BtGattAttr.ISSC_CHAR_RX_UUID);

    /**
     * 连接成功标准 *
     */
    public final static int ACTION_GATT_CONNECTED = 200;
    /**
     * 尝试连接标志 *
     */
    public final static int ACTION_GATT_TRY_CONNECT = 201;
    /**
     * 断开连接状态 *
     */
    public final static int ACTION_GATT_DISCONNECTED = -1;

    public final static int STATE_OPENED = 1; // 打开未连接
    public final static int STATE_CLOSE = 2; // 关闭
    public final static int STATE_BROADCAST = 3; // 接受广播中
    public final static int STATE_CONNECTING = 4;// 正在连接
    public final static int STATE_WRITTING = 5;// 连接上了，正在下传数据

    /**
     * 是否允许广播,当为false的时候,不能执行广播语句 *
     */
    private static boolean isAllowedBroadcast = false;

    // =====================================定义变量=======================================
    private static CsBtUtil instance;
    private BluetoothAdapter mBtAdapter = null; // 蓝牙的适配器
    private BluetoothLeScanner leScanner;
    private BluetoothGatt mBtGatt; // 本手机蓝牙作为中央来使用和处理数据
    private BluetoothGattCharacteristic mWriteCharacteristic; // 用来保存写入到设备数据！
    private BluetoothGattCharacteristic mNotifyCharacteristic; // 用来通知，有数据改变~！
    private static Context mcontext;
    private OnBluetoothListener bluetoothListener = null;

    private int currentConnectState = ACTION_GATT_DISCONNECTED;
    //	private List<ScanFilter> scanFilters=new ArrayList<ScanFilter>();
//	private ScanSettings scanSettings=
    private boolean isSearchStoped;


    // ========凡是透传的设备都需要在这里定义========
    /**
     * Device scan callback. 通过回调来显示是否搜索到 设备和接收到设备的广播,在这里处理以下事件 (1)
     * 保存ListDevices列表 (2) 解包广播包数据
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            LogUtil.e("ble", "LeScanCallback:" + "device=" + device + "  rssi=" + rssi + "  scanRecord=" + scanRecord);
            if (device == null) {
                return;
            }
            // 只有被允许了广播才会执行以下的语句
            if (isAllowedBroadcast) {
                ThreadUtil.executeThread(new Runnable() {
                    @Override
                    public void run() {
                        // 1. ============以下 是用来蓝牙设备的名字和地址=================
                        synchronized (this) {
                        }// end synchronized
                        LogUtil.e(TAG, "mLeScanCallback run...");
                        handleBroadcastInfo(device, rssi, scanRecord);
                    }
                });

            }// end onLeScan
        }
    };


    /**
     * 连接成功后，数据回调的变量 里面回调函数有 ： 1. 数据改变回调函数 2. 数据连接状态的回调函数 3. 设备发现回调函数 4.
     * 读设备函数回调函数 5. 写设备函数回调函数 6. 读取信号回调函数
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            super.onConnectionStateChange(gatt, status, newState);

            LogUtil.i(TAG, " onConnectionStateChange ");
            if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接状态
                LogUtil.i(TAG, "Connected to GATT server. ");
                // Attempts to discover services after successful connection.
                mBtGatt = gatt;
                currentConnectState = ACTION_GATT_CONNECTED;
                boolean result = mBtGatt.discoverServices(); // 尝试去发现设备
                LogUtil.i(TAG, "Attempting to start service discovery:"
                        + result);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.i(TAG, "Disconnected from GATT server.");
                currentConnectState = ACTION_GATT_DISCONNECTED;
                if (bluetoothListener != null) {
                    mNotifyCharacteristic = null;
                    mWriteCharacteristic = null;
                    try {
                        bluetoothListener.disconnectBluetooth();
//					bluetoothListener.bluetoothStateChange(STATE_OPENED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onServicesDiscovered(gatt, status);
            LogUtil.i(TAG, "onServicesDiscovered function");
            if (status == BluetoothGatt.GATT_SUCCESS) { // 连接成功
                LogUtil.i(TAG, "connetting device sucess");
                List<BluetoothGattService> mbtgatt = mBtGatt.getServices();
                // 必须在设备被找到后才可以使用getServices！
                displayGattServices(mbtgatt);

            } else {
                LogUtil.i(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            LogUtil.i(TAG, "onCharacteristicRead ");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            LogUtil.i(TAG, " onCharacteristicWrite ");
            bluetoothListener.afterDataSend(gatt, characteristic, status);
        }

        // 连接之后,对数据格式的判断和解析
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            LogUtil.i(TAG, "onCharacteristicChange");
            // 在这里处理所有的
            handleConnectedInfo(characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            super.onReadRemoteRssi(gatt, rssi, status);
            LogUtil.i(TAG, "onReadRemoteRssi ");
        }

    };

    /**
     * 析构函数
     */
    private CsBtUtil(Context context) {
        mcontext = context;
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // 得到默认的蓝牙适配器

    }


    public static CsBtUtil getInstance(Context context) {
        if (instance != null && mcontext.equals(context)) {
            return instance;
        } else {
            return instance = new CsBtUtil(context);
        }

    }


    // ===================================自定义函数===================================

    /**
     * 设置回调函数
     *
     * @param listener
     */
    public void setBluetoothListener(OnBluetoothListener listener) {
        Log.e("asd", "setBluetoothListener");
        this.bluetoothListener = listener;
        CsBtUtil.isAllowedBroadcast = true;
    }

    /**
     * 打开蓝牙
     *
     * @param isSilent 1 为通知的方式打开蓝牙 2 为静默打开蓝牙
     */
    public void openBluetooth(boolean isSilent) {
        if (!mBtAdapter.isEnabled()) {
            // 方法一打开蓝牙 -- 打开的时候会有提示是否打开蓝牙
            if (!isSilent) {
                Intent enabler = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mcontext.startActivity(enabler);
            } else {
                mBtAdapter.enable();
            }
        }
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        if (mBtAdapter != null) {
            mBtAdapter.disable();
            if (bluetoothListener != null) {
                try {
                    bluetoothListener.bluetoothStateChange(STATE_CLOSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 判断蓝牙是否已经使能
     */
    public boolean isBluetoothEnable() {
        if (mBtAdapter != null) {
            return mBtAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 判断当前是否处于连接状态
     *
     * @return
     */
    public boolean isConnected() {
        LogUtil.e("ble", "currentConnectState：" + currentConnectState);
        if (currentConnectState == ACTION_GATT_CONNECTED) {
            return true;
        } else if (currentConnectState == ACTION_GATT_DISCONNECTED) {
            return false;
        }
        return false;
    }

    /**
     * 判断当前是否处于连接状态
     *
     * @return
     */
    public boolean isTryConnected() {
        if (currentConnectState == ACTION_GATT_TRY_CONNECT) {
            return true;
        } else if (currentConnectState == ACTION_GATT_DISCONNECTED) {
            return false;
        }
        return false;
    }

    /**
     * 搜索获取数据
     */
    public boolean startSearching() {
        boolean isSucess = false;
        isSearchStoped = false;
        isAllowedBroadcast = true;
        if (mBtAdapter != null) {
            if (mBtAdapter.isEnabled()) {
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!isSearchStoped) {
                            stopSearching(false);
//							bluetoothListener.bluetoothStateChange(STATE_OPENED);
                        }
                    }
                };
//				mWriteCharacteristic=null;
//				mNotifyCharacteristic=null;
                isSucess = mBtAdapter.startLeScan(mLeScanCallback);
                isSucess = true;
                timer.schedule(timerTask, 15000);
            }
        }
        return isSucess;
    }

//	/**
//	 * 搜索获取数据
//	 * */
//	public boolean startSearching2() {
//		LogUtil.e(TAG, "startSearching2...");
//		boolean isSucess = false;
//		if (mBtAdapter != null) {
//			if (mBtAdapter.isEnabled()) {
//				isSucess = mBtAdapter.startLeScan(mLeScanCallback);
//			}
//		}
//		return isSucess;
//	}

//	//	/**
////	 * 停止搜索
////	 * */
//	public void stopSearching2() {
//		LogUtil.e(TAG, "stopSearching2...");
//		if (mBtAdapter != null) {
//			if (mBtAdapter.isEnabled() && (mLeScanCallback != null)) {
//				mBtAdapter.stopLeScan(mLeScanCallback);
//			}
//		}
//	}

    /**
     * 停止搜索
     *
     * @param isFinded 是否搜索到结果
     */
    public void stopSearching(boolean isFinded) {
        try {
            if (mBtAdapter != null) {
                if (mBtAdapter.isEnabled() && (mLeScanCallback != null)) {
                    Log.e(TAG, "handler stop searching -> stopSearching()...");
//				leScanner.stopScan(scanCallback);
                    isSearchStoped = true;
                    isAllowedBroadcast = false;
                    bluetoothListener.searchDeviceFailed(isFinded);
                    mBtAdapter.stopLeScan(mLeScanCallback);
//				bluetoothListener.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ========================================连接BLE设备函数======================================
    // ========================================连接BLE设备函数======================================
    // ========================================连接BLE设备函数======================================

    /**
     * 连接函数
     */
    public boolean connectBTDevice(final String address) {
        LogUtil.e("ble", "连接函数");
        if (mBtAdapter == null || address == null) {
            LogUtil.e(TAG, "connectBTDevice param error");
            return false;
        }
        // 假如已经有地址和 BluetoothGatt不为空的时候，直接进入以下连接状态
//		if (address != null && mBtGatt != null) {
//			LogUtil.e("ble","ble====listner:"+bluetoothListener);
////			mBtGatt.beginReliableWrite()
//			try {
//				if (mBtGatt.connect()) { // 连接！//这个重新连接的方法不符合我的要求,故此屏蔽
//                    LogUtil.i(TAG, " connectBTDevice sucess  ");
//                    return true;
//                } else {
//                    return false;
//                }
//			} catch (Exception e) {
//				if( e instanceof DeadObjectException)
//				{
//					LogUtil.e("ble","ble===========DeadObjectException");
//					return false;
//				}
//				else
//				{
//					e.printStackTrace();
//				}
//			}
//		}

        // 通过地址获得远程设备 device
        try {
            final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            if (device == null) {
                LogUtil.e(TAG, "Device not found. Unable to connect.");
                return false;
            }

            // We want to directly connect to the device, so we are setting the
            // autoConnect
            // parameter to false.
            mBtGatt = device.connectGatt(mcontext, false, mGattCallback); // 连接
            // --获得数据会回调mGattCalback中的函数
            if (mBtGatt != null) {
                if (bluetoothListener != null) {

                    bluetoothListener.bluetoothStateChange(STATE_CONNECTING);
                }
                LogUtil.e(TAG, "! Already got BluetoothGatt !");
//			currentConnectState = ACTION_GATT_TRY_CONNECT; // 尝试连接
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 关闭连接
     */
    public void disconnectBTDevice() {
        try {

            if (mBtAdapter == null || mBtGatt == null) {
                return;
            }
            LogUtil.e(TAG, "disconnectBluetoothGatt");
            currentConnectState = ACTION_GATT_DISCONNECTED;
            mBtGatt.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当完成所有操作之后，一定要调用本函数，释放资源
     */
    public void closeBluetoothGatt() {
        try {
            if (mBtGatt == null) {
                return;
            }
            disconnectBTDevice();
            mBtGatt.close();
            mBtGatt = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读函数 -- 从设备中读取信息
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBtAdapter == null || mBtGatt == null || characteristic == null) {
            LogUtil.e(TAG, " readCharacteristic : error ");
            return;
        }
        mBtGatt.readCharacteristic(characteristic);
    }

    /**
     * 写函数，用来把信息写入到远程设备中
     */
    public void writeCharacteristic(byte[] value) {
        writeCharacteristic(mWriteCharacteristic, value);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        boolean res = false;
        try {
            if (mBtGatt == null) {
                LogUtil.e(TAG, "blemBtGatt error");
                return res;
            }
            if (mBtAdapter == null) {
                LogUtil.e(TAG, "blemBtAdapter error");
                return res;
            }
            if (characteristic == null) {
                LogUtil.e(TAG, "bleWriteCharacteristic .error");
                bluetoothListener.disconnectBluetooth();
                return res;
            }
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
//                try {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } else {
//                try {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
            LogUtil.e(TAG, "writeCharacteristic --ble> " + BytesUtil.bytesToHexString(value));
            characteristic.setValue(value);
            res = mBtGatt.writeCharacteristic(characteristic);

            if (bluetoothListener != null) {
                bluetoothListener.bluetoothStateChange(STATE_WRITTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // ========================================私有函数======================================
    // ========================================私有函数======================================
    // ========================================私有函数======================================

    /**
     * 广播的核心处理
     */
    private void handleBroadcastInfo(final BluetoothDevice device,
                                     final int rssi, final byte[] scanRecord) {
        // 先暂停搜索
//		stopSearching2();
        CsDevice bluetoothDevice = new CsDevice();
        // 判断当前是否有实例
        if (device != null && (device.getName() != null)
                && (device.getAddress() != null)) {
            bluetoothDevice.setBtMac(device.getAddress().toString());
            bluetoothDevice.setBtName(device.getName().toString());
            LogUtil.e(TAG, "current device MAC: " + device.getAddress() + "");
            LogUtil.e(TAG, "current device Name: " + device.getName() + "");
        } else {
            LogUtil.i(TAG, "device or device.getName() is null ");
            startSearching();
            return;
        }
        // 判断当前蓝牙的名字是否与我司的蓝牙相互匹配===================================
        if (bluetoothDevice.getBtName().startsWith(VFUCHONG.BT_NAME)) {

            byte[] receiveData = scanRecord; // 得到接收到的广播字节
            if (bluetoothListener != null) {
                try {
                    bluetoothListener.bluetoothStateChange(STATE_BROADCAST);
                    LogUtil.e(TAG, "mLeScanCallback run send broadcast data");
                    bluetoothListener.broadcastData(bluetoothDevice, receiveData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return;
        }
    }

    /**
     * 连接之后处理的核心,用以处理连接之后的数据
     */
    private void handleConnectedInfo(BluetoothGattCharacteristic characteristic) {

        final byte[] data = characteristic.getValue();
        LogUtil.e("ble", "ble===========handleConnectedInfo这里是接收到了数据");
        // 判断是否有数据=============================================
        if (data != null && data.length > 0) {
            if (bluetoothListener != null) {
                bluetoothListener.specialData(data);
                LogUtil.e(TAG, "handleConnectedInfo specialData");
            }
        }
    }

    /**
     * 用以设置接受提示，设置了这个之后， 当有数据接受的时候，会自动触发回调函数
     */
    private void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBtAdapter == null || mBtGatt == null) {
            LogUtil.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBtGatt.setCharacteristicNotification(characteristic, enabled);

        // 假如定义了以下的函数，就可以随时获得从底层读取上来的数据，假如没有下面的代码，需要先写，才可以读！！
        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            LogUtil.e("ble", "bleUUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()");
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(BtGattAttr.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // 使能
            // NOTIFICATION_VALUE值
            mBtGatt.writeDescriptor(descriptor);
        }

        if (UUID_ISSC_RX.equals(characteristic.getUuid())) {
            LogUtil.e("ble", "UUID_ISSC_RX.equals(characteristic.getUuid()");
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(BtGattAttr.CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBtGatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * 就是用来设置Characteristic -- 就是读写节点变量
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        //未获取服务，设置状态为false
//		if (bluetoothListener != null) {
//			bluetoothListener.displayGattServices(false);
//		}
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            if (BtGattAttr.ISSC_SERVICE_UUID.compareToIgnoreCase(uuid) == 0) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                        .getCharacteristics();
                int i = 0;
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    final int charaProp = gattCharacteristic.getProperties();
                    LogUtil.e("ble", charaProp + "===UUID:"
                            + gattCharacteristic.getUuid().toString());
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//						 判断一下UUID
                        if (gattCharacteristic.getUuid().toString()
                                .compareToIgnoreCase(BtGattAttr.ISSC_CHAR_RX_UUID) == 0) {
                            LogUtil.e("ble", "got NOTIFY characteristic ---- i:" + i);
                            mNotifyCharacteristic = gattCharacteristic;
                            setCharacteristicNotification(mNotifyCharacteristic,
                                    true);
                        }
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0
                            || (charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                        // 判断一下UUID
                        if (gattCharacteristic.getUuid().toString()
                                .compareToIgnoreCase(BtGattAttr.ISSC_CHAR_TX_UUID) == 0) {
                            mWriteCharacteristic = gattCharacteristic;
                            LogUtil.e("ble", "got WRITE characteristic !!!!!---- i:" + i);
                            currentConnectState = ACTION_GATT_CONNECTED;
                        }
                    }
                    if ((bluetoothListener != null)
                            && (mNotifyCharacteristic != null)
                            && (mWriteCharacteristic != null)) {
                        LogUtil.e("ble", "currentConnectState:" + currentConnectState);
                        LogUtil.e("ble", "ble=============bluetoothlistener!=null,读,写都不为空i:" + i);
                        bluetoothListener
                                .didConnectedGetWriteNotifyCharacteristic(
                                        mNotifyCharacteristic,
                                        mWriteCharacteristic);
                        break;
                    }
                    i++;
                }
                LogUtil.e("ble", "ble===========break");
                break;
            }

            // Loops through available Characteristics.

        }
        //获取服务，设置状态为true
//		if (bluetoothListener != null) {
//			bluetoothListener.displayGattServices(true);
//		}
    }

    // ========================================自定义类接口======================================
    // ========================================自定义类接口======================================
    // ========================================自定义类接口======================================
    public interface OnBluetoothListener {
        /**
         * 广播获得的数据
         *
         * @param advData
         */
        void broadcastData(CsDevice device, byte[] advData);

        /**
         * 透传的时候捕获读写句柄--透传连接成功之后回调
         *
         * @param notify
         * @param write
         */
        void didConnectedGetWriteNotifyCharacteristic(
                BluetoothGattCharacteristic notify,
                BluetoothGattCharacteristic write);

        /**
         * 蓝牙状态改变函数
         *
         * @param state
         */
        void bluetoothStateChange(int state);

        /**
         * 断开连接函数
         */
        void disconnectBluetooth();

        /**
         * 接收到透传的数据
         */
        void specialData(byte[] data);

        void afterDataSend(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
//		/**
//		 * 取得服务后接收到透传的状态
//		 * @author hsn
//		 */
//		void displayGattServices(boolean flag);

        /**
         * 搜索失败
         */
        void searchDeviceFailed(boolean isFinded);
    }
}
