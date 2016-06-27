package com.example.battleship;

import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;


public class BluetoothChatFragment extends ActionBarActivity {

	private static final int REQUEST_ENABLE_BT = 1;
	static int player=1;
	 private ArrayAdapter<String> mArrayAdapter;
	int a[][] = new int[3][3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item);
        
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	Toast.makeText(BluetoothChatFragment.this, "Device does not support Bluetooth.", Toast.LENGTH_SHORT).show();                    	
        	return;
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        /*Intent data=null;
        int resultCode=0;
        int requestCode=1;
       // onActivityResult(requestCode,resultCode,data);
        
    	if(resultCode != RESULT_OK)
    	{
    		Toast.makeText(MainActivity.this, "Device does not support Bluetooth.", Toast.LENGTH_SHORT).show();                    	
    		return;
    	}
        */
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	// If there are paired devices
    	if (pairedDevices.size() > 0) {
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) {
    	        // Add the name and address to an array adapter to show in a ListView
    	        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
    	        Toast.makeText(BluetoothChatFragment.this, device.getName() + " - " + device.getAddress(), Toast.LENGTH_SHORT).show();  
    	    }
    	}
    	
    	// Register the BroadcastReceiver
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    	
        
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
   		startActivity(discoverableIntent);
    			
    			
        
        for(int i=0;i<3;i++)
        {
        	for(int j=0;j<3;j++)
        		a[i][j]=-1;
        }
        findViewById(R.id.button1).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button2).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button3).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button4).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button5).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button6).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button7).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button8).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.button9).setOnClickListener(mGlobal_OnClickListener);
    }
    
    final OnClickListener mGlobal_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {
        	int img = 0;
            switch(v.getId()) {
                case R.id.button1:
                	if(a[0][0]!=-1)
                	{
                		break;
                	}
                	a[0][0]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                    //Toast.makeText(MainActivity.this, "Button1 clicked.", Toast.LENGTH_SHORT).show();                
                	findViewById(R.id.button1).setBackgroundResource(img);
                    checkResult();
                    break;
                case R.id.button2:
                	if(a[0][1]!=-1)
                	{
                		break;
                	}
                	a[0][1]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button2).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button2 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button3:
                	if(a[0][2]!=-1)
                	{
                		break;
                	}
                	a[0][2]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button3).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button3 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button4:
                	if(a[1][0]!=-1)
                	{
                		break;
                	}
                	a[1][0]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button4).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button4 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button5:
                	if(a[1][1]!=-1)
                	{
                		break;
                	}
                	a[1][1]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button5).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button5 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button6:
                	if(a[1][2]!=-1)
                	{
                		break;
                	}
                	a[1][2]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button6).setBackgroundResource(img);checkResult();
                	//Toast.makeText(MainActivity.this, "Button6 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button7:
                	if(a[2][0]!=-1)
                	{
                		break;
                	}
                	a[2][0]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button7).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button7 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button8:
                	if(a[2][1]!=-1)
                	{
                		break;
                	}
                	a[2][1]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button8).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button8 clicked.", Toast.LENGTH_SHORT).show();                
                break;
                case R.id.button9:
                	if(a[2][2]!=-1)
                	{
                		break;
                	}
                	a[2][2]=player;
                	if(player==1)
                	{
                		//img = R.drawable.cross;
                		player=2;
                	}
                	else
                	{
                		//img = R.drawable.dot;
                		player=1;
                	}
                	findViewById(R.id.button9).setBackgroundResource(img);
                	checkResult();
                	//Toast.makeText(MainActivity.this, "Button9 clicked.", Toast.LENGTH_SHORT).show();                
                break;
            }
        }
    };
    
 // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                
                Toast.makeText(BluetoothChatFragment.this, device.getName() + " - " + device.getAddress(), Toast.LENGTH_SHORT).show();  
            }
        }
    };
    
    public void checkResult()
    {
    	if(check())
    	{
    		findViewById(R.id.button1).setEnabled(false);
            findViewById(R.id.button2).setEnabled(false);
            findViewById(R.id.button3).setEnabled(false);
            findViewById(R.id.button4).setEnabled(false);
            findViewById(R.id.button5).setEnabled(false);
            findViewById(R.id.button6).setEnabled(false);
            findViewById(R.id.button7).setEnabled(false);
            findViewById(R.id.button8).setEnabled(false);
            findViewById(R.id.button9).setEnabled(false);
    	}
    }
    
   

    public boolean check()
    {
    	int count1 = 0;
    	int count2 = 0;
    	int count3,count4,count5=0,count6=0,count7=0,count8=0;
    	for(int i=0;i<3;i++)
    	{
    		count1=0;count2=0;count3=0;count4=0;
    		for(int j=0;j<3;j++)
    		{
    			if(a[i][j]==1)
    			{
    				count1++;
    			}
    			else if(a[i][j]==2)
    			{
    				count2++;
    			}
    			
    			if(a[j][i]==1)
    			{
    				count3++;
    			}
    			else if(a[j][i]==2)
    			{
    				count4++;
    			}
    			if(i+j==2)
    			{
    				if(a[i][j]==1)
    				{
    					count7++;
    				}
    				else if(a[i][j]==2)
    				{
    					count8++;
    				}
    			}
    		}
    		if(count1==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 1 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    		else if(count2==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 2 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    		if(count3==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 1 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    		else if(count4==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 2 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    		
    		if(a[i][i]==1)
    		{
    			count5++;
    		}
    		else if(a[i][i]==2)
    		{
    			count6++;
    		}
    		if(count5==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 1 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    		else if(count6==3)
    		{
    			Toast.makeText(BluetoothChatFragment.this, "Player 2 win", Toast.LENGTH_SHORT).show();                
    			return true;
    		}
    	}
    	if(count7==3)
		{
			Toast.makeText(BluetoothChatFragment.this, "Player 1 win", Toast.LENGTH_SHORT).show(); 
			return true;
		}
		else if(count8==3)
		{
			Toast.makeText(BluetoothChatFragment.this, "Player 2 win", Toast.LENGTH_SHORT).show(); 
			return true;
		}
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
