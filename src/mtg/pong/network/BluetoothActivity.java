package mtg.pong.network;

import java.io.IOException;
import java.util.UUID;

import mtg.pong.Global;
import mtg.pong.MainActivity;
import mtg.pong.OneInstanceActivity;
import mtg.pong.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends OneInstanceActivity {

	public static int REQUEST_ENABLE_BT = 239747;

	public static int REQUEST_ENABLE_DISCOVERABLE = 239748;

	public static UUID MY_UUID = new UUID(239783947l, 937402397l);

	public static String NAME;

	private boolean watingClient = false;

	private final boolean connected = false;

	protected static Handler handler;

	public AcceptThread acceptThead;

	// private BroadcastReceiver btReceiver;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// cannot use static
		NAME = getString(R.string.app_name);
		setContentView(R.layout.activity_bluetooth);
		getWindow().getDecorView().getRootView()
				.setBackgroundColor(Color.BLACK);
		prepareTexts();
		handler = new Handler() {
			@Override
			public void handleMessage(final Message m) {
				if (m.what == 0) {
					Global.toastLong(BluetoothActivity.this,
							BluetoothActivity.this
									.getString(R.string.btConnectionError));
				} else if (m.what == 1) {
					Global.toastLong(BluetoothActivity.this,
							BluetoothActivity.this
									.getString(R.string.btConnected));
					final Intent intent = new Intent(BluetoothActivity.this,
							MainActivity.class);
					intent.putExtra("isHoast", true);
					startActivity(intent);
				}
			}
		};
		// this.btReceiver = new BroadcastReceiver() {
		// @Override
		// public void onReceive(final Context context, final Intent intent) {
		// final String action = intent.getAction();
		// // When discovery finds a device
		// if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
		// // Get the BluetoothDevice object from the Intent
		// final int scMode = intent
		// .getParcelableExtra(BluetoothAdapter.EXTRA_SCAN_MODE);
		// if (scMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
		// final String text = "ta on";
		// final Toast toast = Toast.makeText(
		// BluetoothActivity.this, text,
		// Toast.LENGTH_SHORT);
		// toast.show();
		// }
		// }
		// }
		// };
		// final IntentFilter filter = new IntentFilter(
		// BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		// registerReceiver(this.btReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.processTextState();
	}

	private void processTextState() {
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		final TextView btTxt = (TextView) findViewById(R.id.btStatusView);
		final TextView clientTxt = (TextView) findViewById(R.id.btClientTxt);
		final TextView hostTxt = (TextView) findViewById(R.id.btHostTxt);
		if (btAdapter == null) {
			btTxt.setText(getString(R.string.btStatusError));
			btTxt.setTextColor(Color.RED);
			clientTxt.setVisibility(View.GONE);
			hostTxt.setVisibility(View.GONE);
			return;
		}
		if (btAdapter.isEnabled()) {
			btTxt.setText(getString(R.string.btStatusOn));
			btTxt.setTextColor(Color.BLUE);
			clientTxt.setVisibility(View.VISIBLE);
			hostTxt.setVisibility(View.VISIBLE);
			this.processHostingText();
		} else {
			btTxt.setText(getString(R.string.btStatusOff));
			btTxt.setTextColor(Color.WHITE);
			clientTxt.setVisibility(View.GONE);
			hostTxt.setVisibility(View.GONE);
		}
	}

	public void btClient(final View view) {
		if (this.watingClient) {
			return;
		}
		final Intent intent = new Intent(this, BTClientActivity.class);
		startActivity(intent);
	}

	public void btHost(final View view) {
		if (this.watingClient) {
			return;
		}
		final Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1800);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
	}

	public void turnOnBt(final View view) {
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter != null && !btAdapter.isEnabled()) {
			final Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	public void watingAction(final View view) {
		if (this.connected) {

		} else {
			this.watingClient = false;
			this.processHostingText();
		}
	}

	private void prepareTexts() {
		final Typeface font = Global.getPixelatedFont(this);

		TextView txt = (TextView) findViewById(R.id.btTitle);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.btStatusView);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.btHostTxt);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.btClientTxt);
		txt.setTypeface(font);

		txt = (TextView) findViewById(R.id.cancelHostTxt);
		txt.setTypeface(font);
		txt.setVisibility(View.GONE);

		txt = (TextView) findViewById(R.id.watingClientTxt);
		txt.setTextColor(Color.BLUE);
		txt.setTypeface(font);
		txt.setVisibility(View.GONE);

	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				this.processTextState();
			} else {
				final Toast toast = Toast.makeText(this,
						getString(R.string.btStatusErrorTurningOn),
						Toast.LENGTH_LONG);
				toast.show();
			}
		} else if (requestCode == REQUEST_ENABLE_DISCOVERABLE) {
			if (resultCode != Activity.RESULT_CANCELED) {
				this.watingClient = true;
				if (this.acceptThead == null) {
					try {
						this.acceptThead = new AcceptThread();
					} catch (final IOException e) {
						Global.toastLong(
								this,
								getString(R.string.btStatusErrorTurningDiscoveryOn));
					}
				}
				this.acceptThead.start();
			} else {
				this.watingClient = false;
				Global.toastLong(this,
						getString(R.string.btStatusErrorTurningDiscoveryOn));
			}
			this.processHostingText();
		}
	}

	private void processHostingText() {
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (this.watingClient
				&& btAdapter != null
				&& (btAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE || btAdapter
						.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE)) {

			TextView txt = (TextView) findViewById(R.id.btHostTxt);
			txt.setTextColor(Color.GRAY);

			txt = (TextView) findViewById(R.id.btClientTxt);
			txt.setTextColor(Color.GRAY);

			txt = (TextView) findViewById(R.id.cancelHostTxt);
			txt.setVisibility(View.VISIBLE);

			txt = (TextView) findViewById(R.id.watingClientTxt);
			txt.setVisibility(View.VISIBLE);

		} else {
			this.watingClient = false;
			TextView txt = (TextView) findViewById(R.id.btHostTxt);
			txt.setTextColor(Color.WHITE);

			txt = (TextView) findViewById(R.id.btClientTxt);
			txt.setTextColor(Color.WHITE);

			txt = (TextView) findViewById(R.id.cancelHostTxt);
			txt.setVisibility(View.GONE);

			txt = (TextView) findViewById(R.id.watingClientTxt);
			txt.setVisibility(View.GONE);
			this.cancelAccept();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.cancelAccept();
		// unregisterReceiver(this.btReceiver);
	}

	public void cancelAccept() {
		if (this.acceptThead != null) {
			this.acceptThead.cancel();
		}
	}

	private class AcceptThread extends Thread {
		public final BluetoothServerSocket btServerSocket;
		public final BluetoothAdapter btAdapter = BluetoothAdapter
				.getDefaultAdapter();

		public AcceptThread() throws IOException {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			// MY_UUID is the app's UUID string, also used by the client
			// code
			tmp = this.btAdapter.listenUsingRfcommWithServiceRecord(NAME,
					MY_UUID);

			this.btServerSocket = tmp;
		}

		@Override
		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					socket = this.btServerSocket.accept();
					handler.obtainMessage(1).sendToTarget();
				} catch (final IOException e) {
					handler.sendEmptyMessage(0);
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					// Do work to manage the connection (in a separate thread)
					// manageConnectedSocket(socket);
					// Global.toastLong(BluetoothActivity.this,
					// getString(R.string.btConnectionError));
					try {
						this.btAdapter.cancelDiscovery();
						this.btServerSocket.close();
					} catch (final IOException e) {
						break;
					}
					break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				if (this.btServerSocket != null) {
					this.btServerSocket.close();
				}
			} catch (final IOException e) {
			}
		}
	}

}
