package genius.mohammad.loic;

/**
 * Copyright 2013 Mohammad Adib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import java.net.InetAddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {

	private Button lockURL, lockIP, fireButton;
	private TextView urlTV, selectedTargetTV;
	private ServiceDenier serviceDenier = new ServiceDenier();
	private String selectedTarget = "NONE";
	protected PowerManager.WakeLock mWakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Init all views
		lockURL = (Button) findViewById(R.id.lockOnURLButton);
		lockIP = (Button) findViewById(R.id.lockOnIpButton);
		fireButton = (Button) findViewById(R.id.fireButton);
		urlTV = (TextView) findViewById(R.id.urlTV);
		selectedTargetTV = (TextView) findViewById(R.id.selectedTargetTV);
		// Add listeners
		lockURL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Lock on to URL
				vibrate(20);
				lockOn(urlTV.getText().toString());
			}
		});
		lockIP.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				vibrate(20);
				// Lock on to IP
				EditText ip1 = (EditText) findViewById(R.id.ip1);
				EditText ip2 = (EditText) findViewById(R.id.ip2);
				EditText ip3 = (EditText) findViewById(R.id.ip3);
				EditText ip4 = (EditText) findViewById(R.id.ip4);
				String ip = ip1.getText().toString() + "." + ip2.getText().toString() + "." + ip3.getText().toString() + "." + ip4.getText().toString();
				lockOn(ip);
			}
		});
		addListeners();
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "LOICLock");
		Spinner methodS = (Spinner) findViewById(R.id.methodList);
		methodS.setOnItemSelectedListener(this);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getBoolean("warning2", true)) {
			new AlertDialog.Builder(this).setTitle("Terms of Use").setMessage("LOIC is a tool for network stress testing. The developer assumes no responsibilities for unintended use of this tool. LOIC Responsibly!").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().putBoolean("warning2", false).commit();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().putBoolean("warning2", true).commit();
				}
			}).show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		serviceDenier.stop();
	}

	@Override
	public void onBackPressed() {
		if (ServiceDenier.firing) {
			serviceDenier.stop();
			fireButton.setText("FIRE");
		} else {
			super.onBackPressed();
		}
	}

	private void vibrate(int i) {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(50);
	}

	public void fire(View v) {
		this.mWakeLock.acquire();
		vibrate(50);
		if (selectedTarget.equals("NONE")) {
			Toast.makeText(this, "No target selected!", Toast.LENGTH_SHORT).show();
		} else {
			EditText timeoutET = (EditText) findViewById(R.id.timeoutET);
			EditText portET = (EditText) findViewById(R.id.portET);
			EditText messageET = (EditText) findViewById(R.id.messageET);
			Spinner methodS = (Spinner) findViewById(R.id.methodList);
			EditText threadsET = (EditText) findViewById(R.id.threadsET);
			final Button b = (Button) v;
			if (!ServiceDenier.firing) {
				int timeout = Integer.parseInt(timeoutET.getText().toString());
				int port = Integer.parseInt(portET.getText().toString());
				String message = messageET.getText().toString();
				final int method = methodS.getSelectedItemPosition();
				int threads = Integer.parseInt(threadsET.getText().toString());
				ProgressBar bar = (ProgressBar) findViewById(R.id.speedTrackbar);
				serviceDenier.DDOS(selectedTarget, port, method, threads, timeout, message, bar.getProgress());
				b.setText("STOP");

				Runnable r = new Runnable() {

					public void run() {
						final TextView tvHits = (TextView) findViewById(R.id.statusTV);
						final TextView tvTime = (TextView) findViewById(R.id.statusTVtime);
						final TextView tvPacketsPerSec = (TextView) findViewById(R.id.statusTVpackets);
						while (ServiceDenier.firing) {
							switch (method) {
							case 0:
								MainActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										tvTime.setText("Elapsed Time: " + (System.currentTimeMillis() - TCPSocketThread.startTime) / 1000.0 + "s");
										tvHits.setText("TCP Hits: " + TCPSocketThread.count);
										tvPacketsPerSec.setText("Packets/sec: " + Math.round(TCPSocketThread.count / ((System.currentTimeMillis() - TCPSocketThread.startTime) / 1000.0)));
									}
								});
								break;
							case 1:
								MainActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										tvTime.setText("Elapsed Time: " + (System.currentTimeMillis() - UDPSocketThread.startTime) / 1000.0 + "s");
										tvHits.setText("UDP Hits: " + UDPSocketThread.count);
										tvPacketsPerSec.setText("Packets/sec: " + Math.round(UDPSocketThread.count / ((System.currentTimeMillis() - UDPSocketThread.startTime) / 1000.0)));
									}
								});
								break;
							case 2:
								MainActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										tvTime.setText("Elapsed Time: " + (System.currentTimeMillis() - HTTPSocketThread.startTime) / 1000.0 + "s");
										tvHits.setText("HTTP Hits: " + HTTPSocketThread.count);
										tvPacketsPerSec.setText("Packets/sec: " + Math.round(HTTPSocketThread.count / ((System.currentTimeMillis() - HTTPSocketThread.startTime) / 1000.0)));
									}
								});
								break;
							}

							if (ServiceDenier.error) {
								serviceDenier.stop();
								tvTime.setText("Elapsed Time: " + (System.currentTimeMillis() - TCPSocketThread.startTime) / 1000.0 + "s");
								tvHits.setText("Exception Occurred");
								tvPacketsPerSec.setText("Error - Check log for details");
								b.setText("FIRE");
							}

							try {
								Thread.sleep(50);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						MainActivity.this.mWakeLock.release();
					}
				};
				new Thread(r).start();
			} else {
				serviceDenier.stop();
				b.setText("FIRE");
			}
		}
	}

	public void lockOn(final String address) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					String domain = address.replace("http://", "").replace("www.", "");
					if (domain.length() > 0) {
						selectedTarget = InetAddress.getByName(domain).getHostAddress();
						Runnable r = new Runnable() {

							public void run() {
								selectedTargetTV.setText(selectedTarget);
							}

						};
						MainActivity.this.runOnUiThread(r);
						Log.d("Lock On", selectedTarget);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}

	public void addListeners() {
		final EditText ip1 = (EditText) findViewById(R.id.ip1);
		final EditText ip2 = (EditText) findViewById(R.id.ip2);
		final EditText ip3 = (EditText) findViewById(R.id.ip3);
		final EditText ip4 = (EditText) findViewById(R.id.ip4);

		ip1.addTextChangedListener((new TextWatcher() {
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 2) {
					ip2.requestFocus();
					int i = Integer.parseInt(ip1.getText().toString());
					if (i > 255) {
						ip1.setText("255");
					}
				}
			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 3) {
					ip1.setText(s.toString().substring(0, s.length() - 1));
					int i = Integer.parseInt(ip1.getText().toString());
					if (i > 255) {
						ip1.setText("255");
					}
				}
			}
		}));
		ip2.addTextChangedListener((new TextWatcher() {
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 2) {
					ip3.requestFocus();
					int i = Integer.parseInt(ip2.getText().toString());
					if (i > 255) {
						ip2.setText("255");
					}
				}
			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 3) {
					ip2.setText(s.toString().substring(0, s.length() - 1));
					int i = Integer.parseInt(ip2.getText().toString());
					if (i > 255) {
						ip2.setText("255");
					}
				}
			}
		}));
		ip3.addTextChangedListener((new TextWatcher() {
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 2) {
					ip4.requestFocus();
					int i = Integer.parseInt(ip3.getText().toString());
					if (i > 255) {
						ip3.setText("255");
					}
				}
			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 3) {
					ip3.setText(s.toString().substring(0, s.length() - 1));
					int i = Integer.parseInt(ip3.getText().toString());
					if (i > 255) {
						ip3.setText("255");
					}
				}
			}
		}));
		ip4.addTextChangedListener((new TextWatcher() {
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 3) {
					ip4.setText(s.toString().substring(0, s.length() - 1));
					int i = Integer.parseInt(ip4.getText().toString());
					if (i > 255) {
						ip4.setText("255");
					}
				}
			}
		}));
		ip2.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if (ip2.getText().toString().length() < 1) {
						ip1.requestFocus();
					}
				}
				return false;
			}
		});

		ip3.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if (ip3.getText().toString().length() < 1) {
						ip2.requestFocus();
					}
				}
				return false;
			}
		});

		ip4.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if (ip4.getText().toString().length() < 1) {
						ip3.requestFocus();
					}
				}
				return false;
			}
		});
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EditText timeoutET = (EditText) findViewById(R.id.timeoutET);
		EditText portET = (EditText) findViewById(R.id.portET);
		EditText messageET = (EditText) findViewById(R.id.messageET);
		switch (arg2) {
		case 0:
			timeoutET.setEnabled(true);
			portET.setEnabled(true);
			messageET.setEnabled(true);
			break;
		case 1:
			timeoutET.setEnabled(false);
			portET.setEnabled(true);
			messageET.setEnabled(true);
			break;
		case 2:
			timeoutET.setEnabled(true);
			portET.setText("80");
			portET.setEnabled(false);
			messageET.setEnabled(false);
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
