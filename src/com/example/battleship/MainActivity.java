package com.example.battleship;

import java.util.*;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    static int board[][] = new int[8][8];
    static int enemyBoard[][] = new int[8][8];
    static String TAG = "MainActivity";
    static boolean start = false;
    
    private static final int REQUEST_ENABLE_BT = 2;
	// score initialized to 0.
	static int player=1;
	 private ArrayAdapter<String> mArrayAdapter;
	 private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	    private ArrayAdapter<String> mNewDevicesArrayAdapter;
	int a[][] = new int[3][3];
	int turn =1;
	static View playerView;
	static View enemyView;
	private static BluetoothChatService mChatService = null;
	private StringBuffer mOutStringBuffer;
	private TextView mTitle;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;
	private String mConnectedDeviceName = null;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	static HashMap<Integer, Integer> bt = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	Toast.makeText(MainActivity.this, "Device does not support Bluetooth.", Toast.LENGTH_SHORT).show();                    	
        	return;
        }
        Log.d(TAG,"before enable");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG,"start enable");
        }
        setupGame();

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    //mTitle.setText("Connected to");
                    //mTitle.append(mConnectedDeviceName);
                    Log.d(TAG,"Connected to "+ mConnectedDeviceName);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    //mTitle.setText("Connecting");
                	Log.d(TAG,"Connecting");
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    //mTitle.setText("Not Connected");
                    Log.d(TAG,"Not Connected");
                    break;
                }
                break;
            case MESSAGE_WRITE:
            	Log.d(TAG, "Write Turn: " + turn + " player: " +player);
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //Log.d(TAG,"Writing Message: " + writeMessage +" image: " + img);
                turn = (turn==1)?2:1;
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
            	if(turn == 1)
        		{
        			//img = R.drawable.cross;
        		}
        		else
        		{
        			//img = R.drawable.dot;
        		}
            	Log.d(TAG, "Read Turn: " + turn + " player: " +player);
            	Log.d(TAG,"Readinggg");
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //int number = Integer.parseInt(readMessage);
                //player = number / 10;
                
                //turn = (turn==1)?2:1;
                //int button = number % 10;
                //String btn = String.valueOf(button);
                Log.d(TAG,"Reading Message: " + readMessage);
                if(readMessage.length() > 4)
                {
                	String[] b = readMessage.split(" ");
                	for(int i=0;i<8;i++)
                	{
                		for(int j=0;j<8;j++)
                		{
                			enemyBoard[i][j] = Integer.parseInt(b[(i*8)+j]);
                		}
                	}
                }
                else
                {
                	int btn = Integer.parseInt(readMessage);
                	if(board[(btn-1)/8][(btn-1)%8] == 1)
                	{
                		playerView.findViewById(bt.get(btn-1)).setBackgroundColor(Color.RED);
                	}
                	else
                	{
                		playerView.findViewById(bt.get(btn-1)).setBackgroundColor(Color.GREEN);
                	}
                	
                }
                player = (player==1)?2:1;
                //checkResult();
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                
                Log.d(TAG, "connect device "+ device+" "+ address);
                // Attempt to connect to the device
                mChatService.connect(device,false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupGame();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "BT not enabled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
    	switch (item.getItemId()) {
        case R.id.scan:
        	Log.d(TAG,"Scanning");
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        case R.id.action_settings:
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void setupGame() {
        Log.d(TAG, "setupGame()");

        // Initialize the BluetoothGameService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        	switch(position) {

            	case 0: return FirstFragment.newInstance(position + 1);
            	case 1: return SecondFragment.newInstance(position + 1);
            	default: return SecondFragment.newInstance(position + 1);
        		}
        	}

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                /*case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                    */
            }
            return null;
        }
    }
    
   
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FirstFragment extends Fragment {
    	
    	static int shipId = -1;
    	static int shipNo;
    	static int prevShipNo;
    	static int ship;
    	static int init = 0;
    	static int flag=0;
    	
    	static HashMap<Integer,CoOrdinateList> sh = new HashMap<Integer,CoOrdinateList>();
    	
    	
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FirstFragment newInstance(int sectionNumber) {
        	FirstFragment fragment = new FirstFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FirstFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	playerView = inflater.inflate(R.layout.fragment_1, container, false);
            
            for(int i=0;i<8;i++)
            {
            	for(int j=0;j<8;j++)
            	{
            		board[i][j]=-1;
            	}
            }
            
            bt.put(0, R.id.button1);
            bt.put(1, R.id.button2);
            bt.put(2, R.id.button3);
            bt.put(3, R.id.button4);
            bt.put(4, R.id.button5);
            bt.put(5, R.id.button6);
            bt.put(6, R.id.button7);
            bt.put(7, R.id.button8);
            bt.put(8, R.id.button9);
            bt.put(9, R.id.button10);
            bt.put(10, R.id.button11);
            bt.put(11, R.id.button12);
            bt.put(12, R.id.button13);
            bt.put(13, R.id.button14);
            bt.put(14, R.id.button15);
            bt.put(15, R.id.button16);
            bt.put(16, R.id.button17);
            bt.put(17, R.id.button18);
            bt.put(18, R.id.button19);
            bt.put(19, R.id.button20);
            bt.put(20, R.id.button21);
            bt.put(21, R.id.button22);
            bt.put(22, R.id.button23);
            bt.put(23, R.id.button24);
            bt.put(24, R.id.button25);
            bt.put(25, R.id.button26);
            bt.put(26, R.id.button27);
            bt.put(27, R.id.button28);
            bt.put(28, R.id.button29);
            bt.put(29, R.id.button30);
            bt.put(30, R.id.button31);
            bt.put(31, R.id.button32);
            bt.put(32, R.id.button33);
            bt.put(33, R.id.button34);
            bt.put(34, R.id.button35);
            bt.put(35, R.id.button36);
            bt.put(36, R.id.button37);
            bt.put(37, R.id.button38);
            bt.put(38, R.id.button39);
            bt.put(39, R.id.button40);
            bt.put(40, R.id.button41);
            bt.put(41, R.id.button42);
            bt.put(42, R.id.button43);
            bt.put(43, R.id.button44);
            bt.put(44, R.id.button45);
            bt.put(45, R.id.button46);
            bt.put(46, R.id.button47);
            bt.put(47, R.id.button48);
            bt.put(48, R.id.button49);
            bt.put(49, R.id.button50);
            bt.put(50, R.id.button51);
            bt.put(51, R.id.button52);
            bt.put(52, R.id.button53);
            bt.put(53, R.id.button54);
            bt.put(54, R.id.button55);
            bt.put(55, R.id.button56);
            bt.put(56, R.id.button57);
            bt.put(57, R.id.button58);
            bt.put(58, R.id.button59);
            bt.put(59, R.id.button60);
            bt.put(60, R.id.button61);
            bt.put(61, R.id.button62);
            bt.put(62, R.id.button63);
            bt.put(63, R.id.button64);
            
            playerView.findViewById(R.id.button1).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button2).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button3).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button4).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button5).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button6).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button7).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button8).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button9).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button10).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button11).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button12).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button13).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button14).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button15).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button16).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button17).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button18).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button19).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button20).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button21).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button22).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button23).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button24).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button25).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button26).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button27).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button28).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button29).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button30).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button31).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button32).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button33).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button34).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button35).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button36).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button37).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button38).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button39).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button40).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button41).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button42).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button43).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button44).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button45).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button46).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button47).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button48).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button49).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button50).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button51).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button52).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button53).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button54).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button55).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button56).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button57).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button58).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button59).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button60).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button61).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button62).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button63).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.button64).setOnClickListener(mGlobal_OnClickListener);
            
            playerView.findViewById(R.id.Button01).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.Button02).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.Button03).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.Button04).setOnClickListener(mGlobal_OnClickListener);
            playerView.findViewById(R.id.Button05).setOnClickListener(mGlobal_OnClickListener);
            
            playerView.findViewById(R.id.button65).setOnClickListener(mGlobal_OnClickListener);
            
            return playerView;
        }
        
        private static boolean checkResult()
        {
        	for(int i=0;i<8;i++)
        	{
        		for(int j=0;j<8;j++)
        		{
        			if(board[i][j]!=-1)
        			{
        				return false;
        			}
        		}
        	}
        	return true;
        }
        
        private static void addShip(int a,int b, int c,int d, int e, int f,int g, int h, int i, int n)
        {
        	CustomDrawable cd = new CustomDrawable(Color.LTGRAY);
        	 
        	Log.d(TAG,"aaaa qqqq "+ (n-1)/8 +"," + (n-1)%8 + board[(n-1)/8][(n-1)%8]);
        	init = 0;
        	if(flag==0)//horizontal
        	{
        		flag=1;
        		if(ship == 2 && (n%8)!=0)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			Log.d(TAG,"init: " + init + " shipNO: " + shipNo);
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("1","aaaaa"+ship+" "+ n+ " " + (n-1)/8+" "+ ((n-1)%8+j)+" "+ board[(n-1)/8][(n-1)%8+j]);
        				if(board[(n-1)/8][(n-1)%8+j]==1)
        				{
        					//blocked =1;
        					return;
        				}
        			}
        			
        			for(int j=0;j<ship;j++)
        			{
    					if(((n-1)%8+j)<8){
    						board[(n-1)/8][(n-1)%8+j]=1;
    						Log.d(TAG, "22222222 "+ (n-1)/8 +", " + ((n-1)%8+j) + " =" +board[(n-1)/8][(n-1)%8+j]);
	        			}
    				}
        			
        			/*if(blocked == 0 && first != 0)
            		{
            			rootView.findViewById(a).setBackground(cd);
            			rootView.findViewById(c).setBackground(cd);
            			
        				for(int j=0;j<ship;j++)
            			{
        					
        					if((((n-1)/8)+j)<8){
    	            			board[((n-1)/8)+j][(n-1)%8]=-1;
    	            			Log.d(TAG, "33333 "+ ((n-1)/8+j) +", " + ((n-1)%8) + " =" +board[(n-1)/8+j][(n-1)%8]);
    	        			}
    	    			}
            		}*/
        			//blocked = 0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
        			for(int k=0;k<ship;k++)
        			{
        				Log.d(TAG, "444444 "+ (n-1)/8 +", " + ((n-1)%8+k) + " =" +board[(n-1)/8][(n-1)%8+k]);
        			}
        			Log.d(TAG,"aaaaa " + (n-1)/8 + "," + (n-1)%8 + " "+ board[((n-1)/8)][(n-1)%8]);
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	        				   playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  } 
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(b).setBackgroundColor(Color.BLACK);
        			
        			CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8),((n-1)%8)+k);
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        			
        		}
        		else if(ship==3 && (n%8)<7 && (n%8)!=0)
        		{
        		
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("1","aaaaa"+ship+" "+ n/8+" "+ ((n-1)%8+j)+" "+ board[(n-1)/8][(n-1)%8+j]);
        				if(board[(n-1)/8][(n-1)%8+j]==1)
        				{
        					//blocked =1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
    					if(((n-1)%8+j)<8){
    						board[(n-1)/8][(n-1)%8+j]=1;
	        			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			rootView.findViewById(a).setBackground(cd);
            			rootView.findViewById(c).setBackground(cd);
            			rootView.findViewById(e).setBackground(cd);
            			
        				for(int j=0;j<ship;j++)
            			{
        					
        					if((((n-1)/8)+j)<8){
    	            			board[((n-1)/8)+j][(n-1)%8]=-1;
    	        			}
    	    			}
            		}*/
        			//blocked = 0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	           				Log.d(TAG,"aaaaasssss " + flag);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	           				playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  } 
        			 }
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(b).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(d).setBackgroundColor(Color.BLACK);
        			
        			CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8),((n-1)%8)+k);
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        		else if(ship==4 && (n%8)<6 && (n%8)!=0)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("1","aaaaa"+ship+" "+ n/8+" "+ ((n-1)%8+j)+" "+board[(n-1)/8][(n-1)%8+j]);
        				if(board[(n-1)/8][(n-1)%8+j]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
    					if(((n-1)%8+j)<8){
    						board[(n-1)/8][(n-1)%8+j]=1;
	        			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			rootView.findViewById(a).setBackground(cd);
            			rootView.findViewById(c).setBackground(cd);
            			rootView.findViewById(e).setBackground(cd);
            			rootView.findViewById(g).setBackground(cd);
            			
        				for(int j=0;j<ship;j++)
            			{
        					
        					if((((n-1)/8)+j)<8){
    	            			board[((n-1)/8)+j][(n-1)%8]=-1;
    	        			}
    	    			}
            		}*/
        			//blocked = 0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	           				Log.d(TAG,"aaaaasssss " + flag);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	           				playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(b).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(d).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(f).setBackgroundColor(Color.BLACK);
        			CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8),((n-1)%8)+k);
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        		else if(ship==5 && (n%8)<5 && (n%8)!=0)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("1","aaaaa"+ship+" "+ n/8+" "+ ((n-1)%8+j)+" "+board[(n-1)/8][(n-1)%8+j]);
        				if(board[(n-1)/8][(n-1)%8+j]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
    					if(((n-1)%8+j)<8){
    						board[(n-1)/8][(n-1)%8+j]=1;
	        			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			rootView.findViewById(a).setBackground(cd);
            			rootView.findViewById(c).setBackground(cd);
            			rootView.findViewById(e).setBackground(cd);
            			rootView.findViewById(g).setBackground(cd);
            			rootView.findViewById(i).setBackground(cd);
            			
        				for(int j=0;j<ship;j++)
            			{
        					
        					if((((n-1)/8)+j)<8){
    	            			board[((n-1)/8)+j][(n-1)%8]=-1;
    	        			}
    	    			}
            		}*/
        			//blocked = 0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();    
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	           				Log.d(TAG,"aaaaasssss " + flag);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	           				playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(b).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(d).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(f).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(h).setBackgroundColor(Color.BLACK);
        			CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8),((n-1)%8)+k);
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        	}
        	else if(flag==1)//vertical
        	{
        		flag = 0;
        		if(ship == 2 && n<=56)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			Log.d(TAG,"init: " + init + " shipNO: " + shipNo);
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("3","aaaaa"+ship+" "+ n+" "+ j+ " "+(((n-1)/8)+j)+" "+ ((n-1)%8)+" "+ board[((n-1)/8)+j][(n-1)%8]);
        				if(board[((n-1)/8)+j][(n-1)%8]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
        				if((((n-1)/8)+j)<8)
        				{
        					board[((n-1)/8)+j][(n-1)%8]=1;
        				}
        			}
        			
        			/*if(blocked == 0 && first != 0)
            		{
            			for(int j=0;j<ship;j++)
            			{
            				
                			if((((n-1)%8)+j)<8)
                			{
                				board[(n-1)/8][(n-1)%8+j]=-1;
        	    			}
            			}
            			rootView.findViewById(a).setBackground(cd);
                		rootView.findViewById(b).setBackground(cd);
            		}*/
            		//blocked=0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
            		if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	        				   playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(c).setBackgroundColor(Color.BLACK);
            		CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8)+k,((n-1)%8));
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        		else if(ship==3 && n<=48)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("3","aaaaa"+ship+" "+ (((n-1)/8)+j)+" "+ ((n-1)%8)+" "+ board[((n-1)/8)+j][(n-1)%8]);
        				if(board[((n-1)/8)+j][(n-1)%8]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
        				if((((n-1)/8)+j)<8)
            			{
        					board[((n-1)/8)+j][(n-1)%8]=1;
    	    			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			for(int j=0;j<ship;j++)
            			{
            				
                			if((((n-1)%8)+j)<8)
                			{
                				board[(n-1)/8][(n-1)%8+j]=-1;
        	    			}
            			}
            			rootView.findViewById(a).setBackground(cd);
                		rootView.findViewById(b).setBackground(cd);
            			rootView.findViewById(d).setBackground(cd);
            		}*/
            		//blocked=0;first=1;
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	           				Log.d(TAG,"aaaaasssss " + flag);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	           				playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(c).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(e).setBackgroundColor(Color.BLACK);
            		CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8)+k,((n-1)%8));
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        		else if(ship==4 && n<=40)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("3","aaaaa"+ship+" "+ (((n-1)/8)+j)+" "+ ((n-1)%8)+" "+ board[((n-1)/8)+j][(n-1)%8]);
        				if(board[((n-1)/8)+j][(n-1)%8]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
        				if((((n-1)/8)+j)<8)
            			{
        					board[((n-1)/8)+j][(n-1)%8]=1;
    	    			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			for(int j=0;j<ship;j++)
            			{
            				
                			if((((n-1)%8)+j)<8)
                			{
                				board[(n-1)/8][(n-1)%8+j]=-1;
        	    			}
            			}
            			rootView.findViewById(a).setBackground(cd);
                		rootView.findViewById(b).setBackground(cd);
            			rootView.findViewById(d).setBackground(cd);
            			rootView.findViewById(f).setBackground(cd);
            		}*/
            		//blocked=0;first=1;
        			
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	        				   playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(c).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(e).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(g).setBackgroundColor(Color.BLACK);
            		CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8)+k,((n-1)%8));
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        		else if(ship==5 && n<=32)
        		{
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  if(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   Log.d(TAG,"x: " + x + " y: " + y + " n: " + n);
	        				   if(((x*8)+y+1) == n)
	        				   {
	        					   init = 1;
	        				   }
        				  } 
        			}
        			for(int j=init;j<ship;j++)
        			{
        				Log.d("3","aaaaa"+ship+" "+(((n-1)/8)+j)+" "+ ((n-1)%8)+" "+ board[((n-1)/8)+j][(n-1)%8]);
        				if(board[((n-1)/8)+j][(n-1)%8]==1)
        				{
        					//blocked=1;
        					return;
        				}
        			}
        			for(int j=0;j<ship;j++)
        			{
        				if((((n-1)/8)+j)<8)
            			{
        					board[((n-1)/8)+j][(n-1)%8]=1;
    	    			}
        			}
        			/*if(blocked == 0 && first != 0)
            		{
            			for(int j=0;j<ship;j++)
            			{
            				
                			if((((n-1)%8)+j)<8)
                			{
                				board[(n-1)/8][(n-1)%8+j]=-1;
        	    			}
            			}
            			rootView.findViewById(a).setBackground(cd);
                		rootView.findViewById(b).setBackground(cd);
            			rootView.findViewById(d).setBackground(cd);
            			rootView.findViewById(f).setBackground(cd);
            			rootView.findViewById(h).setBackground(cd);
            		}*/
            		//blocked=0;first=1;
        				
        			playerView.findViewById(shipId).setEnabled(false);
        			
        			if(sh.containsKey(shipNo))
        			{
        				CoOrdinateList p = sh.get(shipNo);
        				
        				Iterator<CoOrdinate> itr=p.CoOrdList.iterator();  
        				  while(itr.hasNext()){  
        					  CoOrdinate cood = itr.next();  
	        				   int x = cood.x;
	        				   int y = cood.y;
	        				   board[x][y] = -1;
	        				   Log.d(TAG,"x: " + x + " y: " + y);
	           				Log.d(TAG,"aaaaasssss " + flag);
	               			
	           				//Log.d(TAG, "222222222222 " + shipNo +" " + p.x + "," +p.y + " " + bt.get((x*8)+y));
	           				playerView.findViewById(bt.get((x*8)+y)).setBackground(cd);
        				  }  
        			}
        			board[((n-1)/8)][(n-1)%8]=1;
        			playerView.findViewById(a).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(c).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(e).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(g).setBackgroundColor(Color.BLACK);
        			playerView.findViewById(i).setBackgroundColor(Color.BLACK);
        			CoOrdinateList cl = new CoOrdinateList();
        			
        			for(int k=0;k<ship;k++)
        			{
        				CoOrdinate cod = new CoOrdinate(((n-1)/8)+k,((n-1)%8));
            			cl.CoOrdList.add(cod);
        			}
        			
        			sh.put(shipNo, cl);
        		}
        	}
        }
        
        final static OnClickListener mGlobal_OnClickListener = new OnClickListener() {
            public void onClick(final View v) {
            	for(int j=0;j<8;j++)
            	{
            		for(int i=0;i<8;i++)
            		{
            			if(board[j][i]==1)
            			{
            				Log.d("qq","["+j+", "+ i+"]"+board[j][i]);
            			}
            			
            		}
            		Log.d("qq","\n");
            	}
            	
                switch(v.getId()) {
                    case R.id.button1:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button1, R.id.button2, R.id.button9, R.id.button3, R.id.button17, R.id.button4, R.id.button25, R.id.button5, R.id.button33, 1);
                    		
                    	}
                    	
                        break;
                    case R.id.button2:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button2, R.id.button3, R.id.button10, R.id.button4, R.id.button18, R.id.button5, R.id.button26, R.id.button6, R.id.button34, 2);
                    		
                    	}
                    	break;
                    case R.id.button3:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button3, R.id.button4, R.id.button11, R.id.button5, R.id.button19, R.id.button6, R.id.button27, R.id.button7, R.id.button35, 3);
                    	
                    	}
                    	break;
                    case R.id.button4:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button4, R.id.button5, R.id.button12, R.id.button6, R.id.button20, R.id.button7, R.id.button28, R.id.button8, R.id.button36, 4);
                    		
                    	}
                    	break;
                    case R.id.button5:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button5, R.id.button6, R.id.button13, R.id.button7, R.id.button21, R.id.button8, R.id.button29, R.id.button8, R.id.button37, 5);
                    		
                    	}
                        break;
                    case R.id.button6:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button6, R.id.button7, R.id.button14, R.id.button8, R.id.button22, R.id.button8, R.id.button30, R.id.button8, R.id.button38, 6);
                    		
                    	}
                    	break;
                    case R.id.button7:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button7, R.id.button8, R.id.button15, R.id.button8, R.id.button23, R.id.button8, R.id.button31, R.id.button8, R.id.button39, 7);
                    	
                    	}
                    	break;
                    case R.id.button8:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button8, R.id.button8, R.id.button16, R.id.button8, R.id.button24, R.id.button8, R.id.button32, R.id.button8, R.id.button40, 8);
                    		
                    	}
                        break;
                    case R.id.button9: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button9, R.id.button10, R.id.button17, R.id.button11, R.id.button25, R.id.button12, R.id.button33, R.id.button13, R.id.button41, 9);
                    		
                    	}
                    	break;
                    case R.id.button10:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button10, R.id.button11, R.id.button18, R.id.button12, R.id.button26, R.id.button13, R.id.button34, R.id.button14, R.id.button42, 10);
                    		
                    	}
                    	break;
                    case R.id.button11:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button11, R.id.button12, R.id.button19, R.id.button13, R.id.button27, R.id.button14, R.id.button35, R.id.button15, R.id.button43, 11);
                    		
                    	}
                        break;
                    case R.id.button12:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button12, R.id.button13, R.id.button20, R.id.button14, R.id.button28, R.id.button15, R.id.button36, R.id.button16, R.id.button44, 12);
                    		
                    	}
                    	break;
                    case R.id.button13:   
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button13, R.id.button14, R.id.button21, R.id.button15, R.id.button29, R.id.button16, R.id.button37, R.id.button16, R.id.button45, 13);
                    		
                    	}
                    	break;
                    case R.id.button14:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button14, R.id.button15, R.id.button22, R.id.button16, R.id.button30, R.id.button16, R.id.button38, R.id.button16, R.id.button46, 14);
                    		
                    	}
                    	break;
                    case R.id.button15:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button15, R.id.button16, R.id.button23, R.id.button16, R.id.button31, R.id.button16, R.id.button39, R.id.button16, R.id.button47, 15);
                    		
                    	}
                        break;
                    case R.id.button16: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button16, R.id.button16, R.id.button24, R.id.button16, R.id.button32, R.id.button16, R.id.button40, R.id.button16, R.id.button48, 16);
                    		
                    	}
                    	break;
                    case R.id.button17:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button17, R.id.button18, R.id.button25, R.id.button19, R.id.button33, R.id.button20, R.id.button41, R.id.button21, R.id.button49, 17);
                    		
                    	}
                    	break;
                    case R.id.button18:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button18, R.id.button19, R.id.button26, R.id.button20, R.id.button34, R.id.button21, R.id.button42, R.id.button22, R.id.button50, 18);
                    		
                    	}
                        break;
                    case R.id.button19:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button19, R.id.button20, R.id.button27, R.id.button21, R.id.button35, R.id.button22, R.id.button43, R.id.button23, R.id.button51, 19);
                    		
                    	}
                    	break;
                    case R.id.button20:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button20, R.id.button21, R.id.button28, R.id.button22, R.id.button36, R.id.button23, R.id.button44, R.id.button24, R.id.button52, 20);
                    		
                    	}
                    	break;
                    case R.id.button21:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button21, R.id.button22, R.id.button29, R.id.button23, R.id.button37, R.id.button24, R.id.button45, R.id.button24, R.id.button53, 21);
                    		
                    	}
                        break;
                    case R.id.button22:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button22, R.id.button23, R.id.button30, R.id.button24, R.id.button38, R.id.button24, R.id.button46, R.id.button24, R.id.button54, 22);
                    		
                    	}
                    	break;
                    case R.id.button23:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button23, R.id.button24, R.id.button31, R.id.button24, R.id.button39, R.id.button24, R.id.button47, R.id.button24, R.id.button55, 23);
                    		
                    	}
                    	break;
                    case R.id.button24:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button24, R.id.button24, R.id.button32, R.id.button24, R.id.button40, R.id.button24, R.id.button48, R.id.button24, R.id.button56, 24);
                    		
                    	}
                    	break;
                    case R.id.button25:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button25, R.id.button26, R.id.button33, R.id.button27, R.id.button41, R.id.button28, R.id.button49, R.id.button29, R.id.button57, 25);
                    		
                    	}
                        break;
                    case R.id.button26: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button26, R.id.button27, R.id.button34, R.id.button28, R.id.button42, R.id.button29, R.id.button50, R.id.button30, R.id.button58, 26);
                    		
                    	}
                    	break;
                    case R.id.button27:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button27, R.id.button28, R.id.button35, R.id.button29, R.id.button43, R.id.button30, R.id.button51, R.id.button31, R.id.button59, 27);
                    		
                    	}
                    	break;
                    case R.id.button28:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button28, R.id.button29, R.id.button36, R.id.button30, R.id.button44, R.id.button31, R.id.button52, R.id.button32, R.id.button60, 28);
                    		
                    	}
                        break;
                    case R.id.button29:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button29, R.id.button30, R.id.button37, R.id.button31, R.id.button45, R.id.button32, R.id.button53, R.id.button32, R.id.button61, 29);
                    	
                    	}
                    	break;
                    case R.id.button30:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button30, R.id.button31, R.id.button38, R.id.button32, R.id.button46, R.id.button32, R.id.button54, R.id.button32, R.id.button62, 30);
                    		
                    	}
                    	break;
                    case R.id.button31: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button31, R.id.button32, R.id.button39, R.id.button32, R.id.button47, R.id.button32, R.id.button55, R.id.button32, R.id.button63, 31);
                    		
                    	}
                        break;
                    case R.id.button32:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button32, R.id.button32, R.id.button40, R.id.button32, R.id.button48, R.id.button32, R.id.button56, R.id.button32, R.id.button64, 32);
                    		
                    	}
                    	break;
                    case R.id.button33:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button33, R.id.button34, R.id.button41, R.id.button35, R.id.button49, R.id.button36, R.id.button57, R.id.button37, R.id.button57, 33);
                    		
                    	}
                    	break;
                    case R.id.button34:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button34, R.id.button35, R.id.button42, R.id.button36, R.id.button50, R.id.button37, R.id.button58, R.id.button38, R.id.button58, 34);
                    		
                    	}
                    	break;
                    case R.id.button35:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button35, R.id.button36, R.id.button43, R.id.button37, R.id.button51, R.id.button38, R.id.button59, R.id.button39, R.id.button59, 35);
                    		
                    	}
                        break;
                    case R.id.button36: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button36, R.id.button37, R.id.button44, R.id.button38, R.id.button52, R.id.button39, R.id.button60, R.id.button40, R.id.button60, 36);
                    		
                    	}
                    	break;
                    case R.id.button37:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button37, R.id.button38, R.id.button45, R.id.button39, R.id.button53, R.id.button40, R.id.button61, R.id.button40, R.id.button61, 37);
                    		
                    	}
                    	break;
                    case R.id.button38:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button38, R.id.button39, R.id.button46, R.id.button40, R.id.button54, R.id.button40, R.id.button62, R.id.button40, R.id.button62, 38);
                    		
                    	}
                        break;
                    case R.id.button39:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button39, R.id.button40, R.id.button47, R.id.button40, R.id.button55, R.id.button40, R.id.button63, R.id.button40, R.id.button63, 39);
                    		
                    	}
                    	break;
                    case R.id.button40:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button40, R.id.button40, R.id.button48, R.id.button40, R.id.button56, R.id.button40, R.id.button64, R.id.button40, R.id.button64, 40);
                    		
                    	}
                    	break;
                    case R.id.button41: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button41, R.id.button42, R.id.button49, R.id.button43, R.id.button57, R.id.button44, R.id.button57, R.id.button45, R.id.button57, 41);
                    		
                    	}
                        break;
                    case R.id.button42:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button42, R.id.button43, R.id.button50, R.id.button44, R.id.button58, R.id.button45, R.id.button58, R.id.button46, R.id.button58, 42);
                    		
                    	}
                    	break;
                    case R.id.button43: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button43, R.id.button44, R.id.button51, R.id.button45, R.id.button59, R.id.button46, R.id.button59, R.id.button47, R.id.button59, 43);
                    		
                    	}
                    	break;
                    case R.id.button44:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button44, R.id.button45, R.id.button52, R.id.button46, R.id.button60, R.id.button47, R.id.button60, R.id.button48, R.id.button60, 44);
                    		
                    	}
                    	break;
                    case R.id.button45:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button45, R.id.button46, R.id.button53, R.id.button47, R.id.button61, R.id.button48, R.id.button61, R.id.button48, R.id.button61, 45);
                    		
                    	}
                        break;
                    case R.id.button46: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button46, R.id.button47, R.id.button54, R.id.button48, R.id.button62, R.id.button48, R.id.button62, R.id.button48, R.id.button62, 46);
                    		
                    	}
                    	break;
                    case R.id.button47:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button47, R.id.button48, R.id.button55, R.id.button48, R.id.button63, R.id.button48, R.id.button63, R.id.button48, R.id.button63, 47);
                    		
                    	}
                    	break;
                    case R.id.button48:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button48, R.id.button48, R.id.button56, R.id.button48, R.id.button64, R.id.button48, R.id.button64, R.id.button48, R.id.button64, 48);
                    		
                    	}
                        break;
                    case R.id.button49: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button49, R.id.button50, R.id.button57, R.id.button51, R.id.button57, R.id.button52, R.id.button57, R.id.button53, R.id.button57, 49);
                    		
                    	}
                    	break;
                    case R.id.button50:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button50, R.id.button51, R.id.button58, R.id.button52, R.id.button58, R.id.button53, R.id.button58, R.id.button54, R.id.button58, 50);
                    		
                    	}
                    	break;
                    case R.id.button51:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button51, R.id.button52, R.id.button59, R.id.button53, R.id.button59, R.id.button54, R.id.button59, R.id.button55, R.id.button59, 51);
                    		
                    	}
                        break;
                    case R.id.button52:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button52, R.id.button53, R.id.button60, R.id.button54, R.id.button60, R.id.button55, R.id.button60, R.id.button56, R.id.button60, 52);
                    		
                    	}
                    	break;
                    case R.id.button53: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button53, R.id.button54, R.id.button61, R.id.button55, R.id.button61, R.id.button56, R.id.button61, R.id.button56, R.id.button61, 53);
                    		
                    	}
                    	break;
                    case R.id.button54:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button54, R.id.button55, R.id.button62, R.id.button56, R.id.button62, R.id.button56, R.id.button62, R.id.button56, R.id.button62, 54);
                    		
                    	}
                    	break;
                    case R.id.button55:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button55, R.id.button56, R.id.button63, R.id.button56, R.id.button63, R.id.button56, R.id.button63, R.id.button56, R.id.button63, 55);
                    		
                    	}
                        break;
                    case R.id.button56: 
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button56, R.id.button56, R.id.button64, R.id.button56, R.id.button64, R.id.button56, R.id.button64, R.id.button56, R.id.button64, 56);
                    		
                    	}
                    	break;
                    case R.id.button57:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button57, R.id.button58, R.id.button57, R.id.button59, R.id.button57, R.id.button60, R.id.button57, R.id.button61, R.id.button57, 57);
                    		
                    	}
                    	break;
                    case R.id.button58:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button58, R.id.button59, R.id.button58, R.id.button60, R.id.button58, R.id.button61, R.id.button58, R.id.button62, R.id.button58, 58);
                    		
                    	}
                        break;
                    case R.id.button59:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button59, R.id.button60, R.id.button59, R.id.button61, R.id.button59, R.id.button62, R.id.button59, R.id.button63, R.id.button59, 59);
                    		
                    	}
                    	break;
                    case R.id.button60:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button60, R.id.button61, R.id.button60, R.id.button62, R.id.button60, R.id.button63, R.id.button60, R.id.button64, R.id.button60, 60);
                    		
                    	}
                    	break;
                    case R.id.button61:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button61, R.id.button62, R.id.button61, R.id.button63, R.id.button61, R.id.button64, R.id.button61, R.id.button64, R.id.button61, 61);
                    		
                    	}
                    	break;
                    case R.id.button62:
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button62, R.id.button63, R.id.button62, R.id.button64, R.id.button62, R.id.button64, R.id.button62, R.id.button64, R.id.button62, 62);
                    		
                    	}
                        break;
                    case R.id.button63:  
                    	if(shipId!=-1)
                    	{
                    		addShip(R.id.button63, R.id.button64, R.id.button63, R.id.button64, R.id.button63, R.id.button64, R.id.button63, R.id.button64, R.id.button63, 63);
                    		
                    	}
                    	break;
                    case R.id.button64:
                    	/*if(shipId!=-1)
                    	{
                    		addShip(R.id.button64, R.id.button64, R.id.button64, R.id.button64, R.id.button64, R.id.button64, R.id.button64, R.id.button64, R.id.button64, 64);
                    		break;
                    	}*/
                    	break;
                    case R.id.Button01:
                    	shipNo=1;
                    	ship=2;
                    	//first=0;
                    	shipId = R.id.Button01;
                    	break;
                    case R.id.Button02:
                    	shipNo=2;
                    	ship=2;
                    	//first=0;
                    	shipId = R.id.Button02;
                    	break;
                    case R.id.Button03:
                    	shipNo=3;
                    	ship=4;
                    	//first=0;
                    	shipId = R.id.Button03;
                    	break;
                    case R.id.Button04:
                    	shipNo=4;
                    	ship=3;
                    	//first=0;
                    	shipId = R.id.Button04;
                    	break;
                    case R.id.Button05:
                    	shipNo=5;
                    	ship=5;
                    	//first=0;
                    	shipId = R.id.Button05;
                    	break;
                    case R.id.button65:
                    	if(playerView.findViewById(R.id.Button01).isEnabled() || playerView.findViewById(R.id.Button02).isEnabled() || playerView.findViewById(R.id.Button03).isEnabled() || playerView.findViewById(R.id.Button04).isEnabled() || playerView.findViewById(R.id.Button05).isEnabled())
                    	{
                    		Toast.makeText(playerView.getContext(), "All Ships are not placed.", Toast.LENGTH_SHORT).show();                    	
                       	}
                    	else
                    	{
                    		String s = "";
                    		for(int i=0;i<8;i++)
                    		{
                    			for(int j=0;j<8;j++)
                    			{
                    				s += board[i][j] + " ";
                    			}
                    		}
                    		mChatService.write(s.getBytes());
                    		start = true;
                    		playerView.findViewById(R.id.button1).setEnabled(false);
                    		playerView.findViewById(R.id.button2).setEnabled(false);
                    		playerView.findViewById(R.id.button3).setEnabled(false);
                    		playerView.findViewById(R.id.button4).setEnabled(false);
                    		playerView.findViewById(R.id.button5).setEnabled(false);
                    		playerView.findViewById(R.id.button6).setEnabled(false);
                    		playerView.findViewById(R.id.button7).setEnabled(false);
                    		playerView.findViewById(R.id.button8).setEnabled(false);
                    		playerView.findViewById(R.id.button9).setEnabled(false);
                    		playerView.findViewById(R.id.button10).setEnabled(false);
                    		playerView.findViewById(R.id.button11).setEnabled(false);
                    		playerView.findViewById(R.id.button12).setEnabled(false);
                    		playerView.findViewById(R.id.button13).setEnabled(false);
                    		playerView.findViewById(R.id.button14).setEnabled(false);
                    		playerView.findViewById(R.id.button15).setEnabled(false);
                    		playerView.findViewById(R.id.button16).setEnabled(false);
                    		playerView.findViewById(R.id.button17).setEnabled(false);
                    		playerView.findViewById(R.id.button18).setEnabled(false);
                    		playerView.findViewById(R.id.button19).setEnabled(false);
                    		playerView.findViewById(R.id.button20).setEnabled(false);
                    		playerView.findViewById(R.id.button21).setEnabled(false);
                    		playerView.findViewById(R.id.button22).setEnabled(false);
                    		playerView.findViewById(R.id.button23).setEnabled(false);
                    		playerView.findViewById(R.id.button24).setEnabled(false);
                    		playerView.findViewById(R.id.button25).setEnabled(false);
                    		playerView.findViewById(R.id.button26).setEnabled(false);
                    		playerView.findViewById(R.id.button27).setEnabled(false);
                    		playerView.findViewById(R.id.button28).setEnabled(false);
                    		playerView.findViewById(R.id.button29).setEnabled(false);
                    		playerView.findViewById(R.id.button30).setEnabled(false);
                    		playerView.findViewById(R.id.button31).setEnabled(false);
                    		playerView.findViewById(R.id.button32).setEnabled(false);
                    		playerView.findViewById(R.id.button33).setEnabled(false);
                    		playerView.findViewById(R.id.button34).setEnabled(false);
                    		playerView.findViewById(R.id.button35).setEnabled(false);
                    		playerView.findViewById(R.id.button36).setEnabled(false);
                    		playerView.findViewById(R.id.button37).setEnabled(false);
                    		playerView.findViewById(R.id.button38).setEnabled(false);
                    		playerView.findViewById(R.id.button39).setEnabled(false);
                    		playerView.findViewById(R.id.button40).setEnabled(false);
                    		playerView.findViewById(R.id.button41).setEnabled(false);
                    		playerView.findViewById(R.id.button42).setEnabled(false);
                    		playerView.findViewById(R.id.button43).setEnabled(false);
                    		playerView.findViewById(R.id.button44).setEnabled(false);
                    		playerView.findViewById(R.id.button45).setEnabled(false);
                    		playerView.findViewById(R.id.button46).setEnabled(false);
                    		playerView.findViewById(R.id.button47).setEnabled(false);
                    		playerView.findViewById(R.id.button48).setEnabled(false);
                    		playerView.findViewById(R.id.button49).setEnabled(false);
                    		playerView.findViewById(R.id.button50).setEnabled(false);
                    		playerView.findViewById(R.id.button51).setEnabled(false);
                    		playerView.findViewById(R.id.button52).setEnabled(false);
                    		playerView.findViewById(R.id.button53).setEnabled(false);
                    		playerView.findViewById(R.id.button54).setEnabled(false);
                    		playerView.findViewById(R.id.button55).setEnabled(false);
                    		playerView.findViewById(R.id.button56).setEnabled(false);
                    		playerView.findViewById(R.id.button57).setEnabled(false);
                    		playerView.findViewById(R.id.button58).setEnabled(false);
                    		playerView.findViewById(R.id.button59).setEnabled(false);
                    		playerView.findViewById(R.id.button60).setEnabled(false);
                    		playerView.findViewById(R.id.button61).setEnabled(false);
                    		playerView.findViewById(R.id.button62).setEnabled(false);
                    		playerView.findViewById(R.id.button63).setEnabled(false);
                    		playerView.findViewById(R.id.button64).setEnabled(false);
                    	}
                    	break;
                }
            }
        };
        
    }

    
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SecondFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int count =0;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SecondFragment newInstance(int sectionNumber) {
        	SecondFragment fragment = new SecondFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SecondFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
             enemyView = inflater.inflate(R.layout.fragment_2, container, false);
             
            enemyView.findViewById(R.id.button1).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button2).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button3).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button4).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button5).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button6).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button7).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button8).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button9).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button10).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button11).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button12).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button13).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button14).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button15).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button16).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button17).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button18).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button19).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button20).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button21).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button22).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button23).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button24).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button25).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button26).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button27).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button28).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button29).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button30).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button31).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button32).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button33).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button34).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button35).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button36).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button37).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button38).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button39).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button40).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button41).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button42).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button43).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button44).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button45).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button46).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button47).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button48).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button49).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button50).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button51).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button52).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button53).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button54).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button55).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button56).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button57).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button58).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button59).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button60).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button61).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button62).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button63).setOnClickListener(mGlobal_OnClickListener);
            enemyView.findViewById(R.id.button64).setOnClickListener(mGlobal_OnClickListener);
            
            
            return enemyView;
        }
        
        final static OnClickListener mGlobal_OnClickListener = new OnClickListener() {
            public void onClick(final View v) {
            	if(start == false)
            	{
            		return;
            	}
            	switch(v.getId()) {
                	case R.id.button1:
                		if(enemyBoard[0][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button1).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button1).setBackgroundColor(Color.GREEN);
                    	}
                		String s = "1";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button2:
                		if(enemyBoard[0][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button2).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button2).setBackgroundColor(Color.GREEN);
                    	}
                		s = "2";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button3:
                		if(enemyBoard[0][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button3).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button3).setBackgroundColor(Color.GREEN);
                    	}
                		s = "3";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button4:
                		if(enemyBoard[0][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button4).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button4).setBackgroundColor(Color.GREEN);
                    	}
                		s = "4";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button5:
                		if(enemyBoard[0][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button5).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button5).setBackgroundColor(Color.GREEN);
                    	}
                		s = "5";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button6:
                		if(enemyBoard[0][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button6).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button6).setBackgroundColor(Color.GREEN);
                    	}
                		s = "6";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button7:
                		if(enemyBoard[0][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button7).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button7).setBackgroundColor(Color.GREEN);
                    	}
                		s = "7";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button8:
                		if(enemyBoard[0][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button8).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button8).setBackgroundColor(Color.GREEN);
                    	}
                		s = "8";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button9:
                		if(enemyBoard[1][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button9).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button9).setBackgroundColor(Color.GREEN);
                    	}
                		s = "9";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button10:
                		if(enemyBoard[1][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button10).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button10).setBackgroundColor(Color.GREEN);
                    	}
                		s = "10";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button11:
                		if(enemyBoard[1][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button11).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button11).setBackgroundColor(Color.GREEN);
                    	}
                		s = "11";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button12:
                		if(enemyBoard[1][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button12).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button12).setBackgroundColor(Color.GREEN);
                    	}
                		s = "12";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button13:
                		if(enemyBoard[1][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button13).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button13).setBackgroundColor(Color.GREEN);
                    	}
                		s = "13";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button14:
                		if(enemyBoard[1][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button14).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button14).setBackgroundColor(Color.GREEN);
                    	}
                		s = "14";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button15:
                		if(enemyBoard[1][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button15).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button15).setBackgroundColor(Color.GREEN);
                    	}
                		s = "15";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button16:
                		if(enemyBoard[1][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button16).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button16).setBackgroundColor(Color.GREEN);
                    	}
                		s = "16";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button17:
                		if(enemyBoard[2][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button17).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button17).setBackgroundColor(Color.GREEN);
                    	}
                		s = "17";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button18:
                		if(enemyBoard[2][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button18).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button18).setBackgroundColor(Color.GREEN);
                    	}
                		s = "18";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button19:
                		if(enemyBoard[2][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button19).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button19).setBackgroundColor(Color.GREEN);
                    	}
                		s = "19";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button20:
                		if(enemyBoard[2][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button20).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button20).setBackgroundColor(Color.GREEN);
                    	}
                		s = "20";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button21:
                		if(enemyBoard[2][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button21).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button21).setBackgroundColor(Color.GREEN);
                    	}
                		s = "21";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button22:
                		if(enemyBoard[2][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button22).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button22).setBackgroundColor(Color.GREEN);
                    	}
                		s = "22";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button23:
                		if(enemyBoard[2][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button23).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button23).setBackgroundColor(Color.GREEN);
                    	}
                		s = "23";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button24:
                		if(enemyBoard[2][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button24).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button24).setBackgroundColor(Color.GREEN);
                    	}
                		s = "24";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button25:
                		if(enemyBoard[3][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button25).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button25).setBackgroundColor(Color.GREEN);
                    	}
                		s = "25";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button26:
                		if(enemyBoard[3][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button26).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button26).setBackgroundColor(Color.GREEN);
                    	}
                		s = "26";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button27:
                		if(enemyBoard[3][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button27).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button27).setBackgroundColor(Color.GREEN);
                    	}
                		s = "27";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button28:
                		if(enemyBoard[3][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button28).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button28).setBackgroundColor(Color.GREEN);
                    	}
                		s = "28";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button29:
                		if(enemyBoard[3][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button29).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button29).setBackgroundColor(Color.GREEN);
                    	}
                		s = "29";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button30:
                		if(enemyBoard[3][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button30).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button30).setBackgroundColor(Color.GREEN);
                    	}
                		s = "30";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button31:
                		if(enemyBoard[3][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button31).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button31).setBackgroundColor(Color.GREEN);
                    	}
                		s = "31";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button32:
                		if(enemyBoard[3][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button32).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button32).setBackgroundColor(Color.GREEN);
                    	}
                		s = "32";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button33:
                		if(enemyBoard[4][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button33).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button33).setBackgroundColor(Color.GREEN);
                    	}
                		s = "33";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button34:
                		if(enemyBoard[4][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button34).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button34).setBackgroundColor(Color.GREEN);
                    	}
                		s = "34";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button35:
                		if(enemyBoard[4][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button35).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button35).setBackgroundColor(Color.GREEN);
                    	}
                		s = "35";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button36:
                		if(enemyBoard[4][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button36).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button36).setBackgroundColor(Color.GREEN);
                    	}
                		s = "36";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button37:
                		if(enemyBoard[4][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button37).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button37).setBackgroundColor(Color.GREEN);
                    	}
                		s = "37";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button38:
                		if(enemyBoard[4][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button38).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button38).setBackgroundColor(Color.GREEN);
                    	}
                		s = "38";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button39:
                		if(enemyBoard[4][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button39).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button39).setBackgroundColor(Color.GREEN);
                    	}
                		s = "39";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button40:
                		if(enemyBoard[4][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button40).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button40).setBackgroundColor(Color.GREEN);
                    	}
                		s = "40";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button41:
                		if(enemyBoard[5][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button41).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button41).setBackgroundColor(Color.GREEN);
                    	}
                		s = "41";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button42:
                		if(enemyBoard[5][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button42).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button42).setBackgroundColor(Color.GREEN);
                    	}
                		s = "42";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button43:
                		if(enemyBoard[5][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button43).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button43).setBackgroundColor(Color.GREEN);
                    	}
                		s = "43";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button44:
                		if(enemyBoard[5][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button44).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button44).setBackgroundColor(Color.GREEN);
                    	}
                		s = "44";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button45:
                		if(enemyBoard[5][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button45).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button45).setBackgroundColor(Color.GREEN);
                    	}
                		s = "45";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button46:
                		if(enemyBoard[5][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button46).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button46).setBackgroundColor(Color.GREEN);
                    	}
                		s = "46";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button47:
                		if(enemyBoard[5][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button47).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button47).setBackgroundColor(Color.GREEN);
                    	}
                		s = "47";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button48:
                		if(enemyBoard[5][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button48).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button48).setBackgroundColor(Color.GREEN);
                    	}
                		s = "48";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button49:
                		if(enemyBoard[6][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button49).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button49).setBackgroundColor(Color.GREEN);
                    	}
                		s = "49";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button50:
                		if(enemyBoard[6][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button50).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button50).setBackgroundColor(Color.GREEN);
                    	}
                		s = "50";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button51:
                		if(enemyBoard[6][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button51).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button51).setBackgroundColor(Color.GREEN);
                    	}
                		s = "51";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button52:
                		if(enemyBoard[6][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button52).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button52).setBackgroundColor(Color.GREEN);
                    	}
                		s = "52";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button53:
                		if(enemyBoard[6][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button53).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button53).setBackgroundColor(Color.GREEN);
                    	}
                		s = "53";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button54:
                		if(enemyBoard[6][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button54).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button54).setBackgroundColor(Color.GREEN);
                    	}
                		s = "54";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button55:
                		if(enemyBoard[6][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button55).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button55).setBackgroundColor(Color.GREEN);
                    	}
                		s = "55";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button56:
                		if(enemyBoard[6][7]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button56).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button56).setBackgroundColor(Color.GREEN);
                    	}
                		s = "56";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button57:
                		if(enemyBoard[7][0]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button57).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button57).setBackgroundColor(Color.GREEN);
                    	}
                		s = "57";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button58:
                		if(enemyBoard[7][1]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button58).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button58).setBackgroundColor(Color.GREEN);
                    	}
                		s = "58";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button59:
                		if(enemyBoard[7][2]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button59).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button59).setBackgroundColor(Color.GREEN);
                    	}
                		s = "59";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button60:
                		if(enemyBoard[7][3]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button60).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button60).setBackgroundColor(Color.GREEN);
                    	}
                		s = "60";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button61:
                		if(enemyBoard[7][4]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button61).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button61).setBackgroundColor(Color.GREEN);
                    	}
                		s = "61";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button62:
                		if(enemyBoard[7][5]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button62).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button62).setBackgroundColor(Color.GREEN);
                    	}
                		s = "62";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button63:
                		if(enemyBoard[7][6]!=-1)
                    	{
                			count++;
                    		v.findViewById(R.id.button63).setBackgroundColor(Color.RED);
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button63).setBackgroundColor(Color.GREEN);
                    	}
                		s = "63";
                		mChatService.write(s.getBytes());
                		break;
                	case R.id.button64:
                		if(enemyBoard[7][7]!=-1)
                    	{
                    		v.findViewById(R.id.button64).setBackgroundColor(Color.RED);
                    		count++;
                    	}
                    	else
                    	{
                    		v.findViewById(R.id.button64).setBackgroundColor(Color.GREEN);
                    	}
                		s = "64";
                		mChatService.write(s.getBytes());
                		break;
            	}
            	if(count==16)
            	{
            		Toast.makeText(enemyView.getContext(), "Your Planet is doomed!!!", Toast.LENGTH_SHORT).show();                    	

            		enemyView.findViewById(R.id.button1).setEnabled(false);
            		enemyView.findViewById(R.id.button2).setEnabled(false);
            		enemyView.findViewById(R.id.button3).setEnabled(false);
            		enemyView.findViewById(R.id.button4).setEnabled(false);
            		enemyView.findViewById(R.id.button5).setEnabled(false);
            		enemyView.findViewById(R.id.button6).setEnabled(false);
            		enemyView.findViewById(R.id.button7).setEnabled(false);
            		enemyView.findViewById(R.id.button8).setEnabled(false);
            		enemyView.findViewById(R.id.button9).setEnabled(false);
            		enemyView.findViewById(R.id.button10).setEnabled(false);
            		enemyView.findViewById(R.id.button11).setEnabled(false);
            		enemyView.findViewById(R.id.button12).setEnabled(false);
            		enemyView.findViewById(R.id.button13).setEnabled(false);
            		enemyView.findViewById(R.id.button14).setEnabled(false);
            		enemyView.findViewById(R.id.button15).setEnabled(false);
            		enemyView.findViewById(R.id.button16).setEnabled(false);
            		enemyView.findViewById(R.id.button17).setEnabled(false);
            		enemyView.findViewById(R.id.button18).setEnabled(false);
            		enemyView.findViewById(R.id.button19).setEnabled(false);
            		enemyView.findViewById(R.id.button20).setEnabled(false);
            		enemyView.findViewById(R.id.button21).setEnabled(false);
            		enemyView.findViewById(R.id.button22).setEnabled(false);
            		enemyView.findViewById(R.id.button23).setEnabled(false);
            		enemyView.findViewById(R.id.button24).setEnabled(false);
            		enemyView.findViewById(R.id.button25).setEnabled(false);
            		enemyView.findViewById(R.id.button26).setEnabled(false);
            		enemyView.findViewById(R.id.button27).setEnabled(false);
            		enemyView.findViewById(R.id.button28).setEnabled(false);
            		enemyView.findViewById(R.id.button29).setEnabled(false);
            		enemyView.findViewById(R.id.button30).setEnabled(false);
            		enemyView.findViewById(R.id.button31).setEnabled(false);
            		enemyView.findViewById(R.id.button32).setEnabled(false);
            		enemyView.findViewById(R.id.button33).setEnabled(false);
            		enemyView.findViewById(R.id.button34).setEnabled(false);
            		enemyView.findViewById(R.id.button35).setEnabled(false);
            		enemyView.findViewById(R.id.button36).setEnabled(false);
            		enemyView.findViewById(R.id.button37).setEnabled(false);
            		enemyView.findViewById(R.id.button38).setEnabled(false);
            		enemyView.findViewById(R.id.button39).setEnabled(false);
            		enemyView.findViewById(R.id.button40).setEnabled(false);
            		enemyView.findViewById(R.id.button41).setEnabled(false);
            		enemyView.findViewById(R.id.button42).setEnabled(false);
            		enemyView.findViewById(R.id.button43).setEnabled(false);
            		enemyView.findViewById(R.id.button44).setEnabled(false);
            		enemyView.findViewById(R.id.button45).setEnabled(false);
            		enemyView.findViewById(R.id.button46).setEnabled(false);
            		enemyView.findViewById(R.id.button47).setEnabled(false);
            		enemyView.findViewById(R.id.button48).setEnabled(false);
            		enemyView.findViewById(R.id.button49).setEnabled(false);
            		enemyView.findViewById(R.id.button50).setEnabled(false);
            		enemyView.findViewById(R.id.button51).setEnabled(false);
            		enemyView.findViewById(R.id.button52).setEnabled(false);
            		enemyView.findViewById(R.id.button53).setEnabled(false);
            		enemyView.findViewById(R.id.button54).setEnabled(false);
            		enemyView.findViewById(R.id.button55).setEnabled(false);
            		enemyView.findViewById(R.id.button56).setEnabled(false);
            		enemyView.findViewById(R.id.button57).setEnabled(false);
            		enemyView.findViewById(R.id.button58).setEnabled(false);
            		enemyView.findViewById(R.id.button59).setEnabled(false);
            		enemyView.findViewById(R.id.button60).setEnabled(false);
            		enemyView.findViewById(R.id.button61).setEnabled(false);
            		enemyView.findViewById(R.id.button62).setEnabled(false);
            		enemyView.findViewById(R.id.button63).setEnabled(false);
            		enemyView.findViewById(R.id.button64).setEnabled(false);
            		//getActivity().finish();
            	}
            }
        };
    }
}
