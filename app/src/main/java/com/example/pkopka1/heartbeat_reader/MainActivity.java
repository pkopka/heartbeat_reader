package com.example.pkopka1.heartbeat_reader;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.SurfaceView;
import android.view.WindowManager;
import org.opencv.core.Mat;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used to load the 'native-lib' library on application startup.
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private Mat                  mRgba;
    private long                 value;
    private int                  n_frame=0;
    public double[]              signal;
    private int                  N=64;
    private long                 time_0=0;
    private long                 time_delta;
    private double               time_sum;




    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {







        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tutorial1_surface_wiew);




        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);

//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);




    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba =  inputFrame.rgba();
        int rows = mRgba.rows(); //Calculates number of rows
        int cols = mRgba.cols(); //Calculates number of columns
        int ch = mRgba.channels(); //Calculates number of channels (Grayscale: 1, RGB: 3, etc.)
        double[] signal;
        for (int i=0; i<rows; i++)
        {
            for (int j=0; j<cols; j++)
            {
                double[] data = mRgba.get(i, j); //Stores element in an array
                value += data[0]; //Pixel modification done here

            }
        }
        signal = new double[N];
        signal[n_frame] = (double)value;
        time_delta = SystemClock.currentThreadTimeMillis() - time_0;
        time_sum +=time_delta;
        n_frame+=1;
        if(n_frame== N){
            double[] window;
            n_frame = 0;
            FFT fft = new FFT(N);
            window = fft.getWindow();
            double[] im = new double[N];
            double[] abs = new double[N/2-1];
            double[] list;
            double fs;
            fs = 1/(time_sum*0.001/N);
//            fs =27.733333333333334;
            time_sum=0;
//
//            list = new double[]{0.95339698,0.95065733,0.95674552,0.95673736,0.95909777,0.95764254
//                    ,0.95937607,0.96368052,0.96247486,0.96400233,0.96765175,0.96994608
//                    ,0.96861927,0.96773337,0.96286204,0.9535541, 0.94053383,0.94181332
//                    ,0.94142655,0.94674046,0.95173539,0.95188543,0.95454553,0.95109461
//                    ,0.95430238,0.95353192,0.95494513,0.95303431,0.95806691,0.9578089
//                    ,0.95924602,0.95661421,0.96486175,0.96524852,0.96741417,0.96460866
//                    ,0.96630525,0.96621729,0.95819318,0.94678478,0.94115819,0.9393624
//                    ,0.94034671,0.94210097,0.94500404,0.95181163,0.9521031, 0.95434729
//                    ,0.95336398,0.95574537,0.95585068,0.95787435,0.95689162,0.95710841
//                    ,0.95425518,0.95812873,0.95777508,0.9605781, 0.95946425,0.96654536
//                    ,0.96682444,0.9680063, 0.96014268,0.94905689,0.94707031,0.94521096
//                    ,0.94532418,0.94914685,0.95061027,0.95309157,0.95397795,0.95419069
//                    ,0.95384419,0.95458551,0.95518735,0.95652358,0.95738241,0.95759816
//                    ,0.95833228,0.96163002,0.96551733,0.96535465,0.96836281,0.96671713
//                    ,0.96755111,0.96984985,0.96638268,0.95795062,0.95396504,0.95089024
//                    ,0.9532706, 0.95310588,0.95952051,0.96034678,0.9646374, 0.96449396
//                    ,0.96984674,0.96863455,0.96974135,0.96790532,0.9680762, 0.9702038
//                    ,0.97031687,0.97277261,0.97367223,0.97195083,0.97228898,0.97521089
//                    ,0.97508618,0.97298509,0.97197719,0.96195159,0.95205115,0.94196202
//                    ,0.94535503,0.94542845,0.94780058,0.94797052,0.95559756,0.95462285
//                    ,0.95579473,0.95236905,0.95434618,0.95402519,0.96175224,0.96507523
//                    ,0.96493157,0.96648258};
            for(int i=1; i<N; i++){
             im[i] = 0;}

            fft.fft(signal,im);
            Log.d("this is my array", "arr real: " + Arrays.toString(signal));
            Log.d("this is my array", "arr im: " + Arrays.toString(im));
            for(int i=0; i<N/2-1; i++){
                abs[i] =2.0/N*Math.pow(Math.pow(signal[i+1],2)+Math.pow(im[i+1],2),0.5);}

            int index = 0;
            double largest = Double.MIN_VALUE;
            for ( int i = 0; i < abs.length; i++ )
            {
                if ( abs[i] > largest )
                {
                    largest = abs[i];
                    index = i;
                }
            }
            double bpm;
            bpm = ((0.5*fs/(N/2))*(index+1))*60;
//            for(int i=1; i<N; i++){
//                signal[i] = 0;}
            Log.d("this is my array", "arr abs: " + Arrays.toString(abs));
            Log.d("Max value", "index: " + Integer.toString(index));
            Log.d("Fraquwency", "fs: " + Double.toString(fs));
            Log.d("delta ", "bmp: " + Double.toString(((0.5*fs/(N/2)))*60));
//
            Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
            myIntent.putExtra("bpm", String.format("%.2f", bpm)); //Optional parameters
            MainActivity.this.startActivity(myIntent);







        }

        String strInt = Integer.toString(n_frame);
        Log.w("Number_of_frame :", strInt);

        String strTime = Long.toString(time_delta);
        time_0 = SystemClock.currentThreadTimeMillis();
        Log.w("Time :",strTime);
        String strLong = Long.toString(value);
        Log.w("Sum",strLong);

        return mRgba;
    }
}