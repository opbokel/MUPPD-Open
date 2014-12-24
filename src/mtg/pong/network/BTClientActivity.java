package mtg.pong.network;

import java.io.IOException;
import java.util.Set;

import mtg.pong.Global;
import mtg.pong.OneInstanceListActivity;
import mtg.pong.R;
import mtg.pong.menu.BTClientAdapter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class BTClientActivity extends OneInstanceListActivity {

	private BroadcastReceiver btReceiver;

	ConnectThread cThread;

	protected static Handler handler;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bt_client);
		getWindow().getDecorView().getRootView()
				.setBackgroundColor(Color.BLACK);
		final BTClientAdapter adapter = new BTClientAdapter(this);
		setListAdapter(adapter);
		this.resetMenu();
		this.btReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					final BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a
					// ListView
					adapter.add(device.getName() + "\n" + device.getAddress());
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					adapter.updateItem(0, getString(R.string.findBTHost));
				}
			}
		};
		handler = new Handler() {
			@Override
			public void handleMessage(final Message m) {
				Global.toastLong(BTClientActivity.this, BTClientActivity.this
						.getString(R.string.btConnectionError));
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(this.btReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(this.btReceiver, filter);
	}

	private void resetMenu() {
		final BTClientAdapter listAdapter = (BTClientAdapter) getListAdapter();
		listAdapter.clear();
		listAdapter.add(getString(R.string.findBTHost));
		listAdapter.add("* * * * * *");
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		final Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (final BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				listAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}
	}

	@Override
	public void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		stopCthread();
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		final TextView tView = (TextView) v;
		final BTClientAdapter listAdapter = (BTClientAdapter) getListAdapter();
		if (position == 0) {
			if (btAdapter.isDiscovering()) {
				btAdapter.cancelDiscovery();
				listAdapter
						.updateItem(position, getString(R.string.findBTHost));
			} else {
				this.resetMenu();
				listAdapter.updateItem(position,
						getString(R.string.findBTHostStop));
				btAdapter.startDiscovery();
			}
		} else if (position > 1) {
			final String address = String.valueOf(tView.getText()).split("\n")[1];
			this.startCthread(address);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(this.btReceiver);
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		btAdapter.cancelDiscovery();
		stopCthread();
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket btSocket;
		private final BluetoothDevice btServerDevice;
		public final BluetoothAdapter btAdapter = BluetoothAdapter
				.getDefaultAdapter();

		public ConnectThread(final BluetoothDevice device) throws IOException {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			this.btServerDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			// MY_UUID is the app's UUID string, also used by the server
			// code
			tmp = device
					.createRfcommSocketToServiceRecord(BluetoothActivity.MY_UUID);

			this.btSocket = tmp;
		}

		@Override
		public void run() {
			// Cancel discovery because it will slow down the connection
			this.btAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				this.btSocket.connect();
				// final Toast toast = Toast.makeText(BTClientActivity.this,
				// getString(R.string.btConnected), Toast.LENGTH_LONG);
				// toast.show();
			} catch (final IOException connectException) {
				handler.sendEmptyMessage(0);
				// Unable to connect; close the socket and get out
				try {
					this.btSocket.close();
				} catch (final IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			// manageConnectedSocket(this.btServerDevice);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				this.btSocket.close();
			} catch (final IOException e) {
			}
		}
	}

	public void stopCthread() {
		if (this.cThread != null) {
			this.cThread.cancel();
		}
	}

	public void startCthread(final String address) {
		if (this.cThread == null) {
			final BluetoothDevice btServerDevice = BluetoothAdapter
					.getDefaultAdapter().getRemoteDevice(address);
			try {
				this.cThread = new ConnectThread(btServerDevice);
			} catch (final IOException e) {
				Global.toastLong(this, getString(R.string.btConnectionError));
			}
		}
		this.cThread.start();
	}
}
