package com.rohanjain.trackbeat;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.jtransforms.fft.DoubleFFT_1D;


public class DataCollection extends ActionBarActivity implements CameraBridgeViewBase.CvCameraViewListener {

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    private CameraBridgeViewBase openCvCameraView;
    private CascadeClassifier cascadeClassifier;
    //private Mat grayscaleImage;
    private Mat nextImg;
    private int absoluteFaceSize;
    //private int screen_height;
    //private int screen_width;
    private Point p1;
    private Point p2;
    private Point p3;
    private Point p4;
    private int frameNo = 0;
    private MatOfPoint2f prevPoints;
    //private MatOfPoint corners = new MatOfPoint();
    private Mat prevFrame;
    private int FaceDetCount = 0;
    // private Mat tracks;
    //private List<MatOfPoint2f> tracks;
    private int track_count = 0;
    private int FLAG=0;
    private double temp[];
    private ArrayList<double[]> trackArrayList;
    private int T=10*10;
    private int N;
    private int Nmax=30;
    private double trackArray[][];
    private PrincipalComponentAnalysis pca;
    private ArrayList<double[]> eigTrackArrayList;
    private double prevtime;
    private int temp_count = 0;
    private double actual_fps;
    private ArrayList<double[]> fftArrayList;
    private DoubleFFT_1D fft;
    private double eigTrackArray[][];
    private double filtera[] = { -2.4734, 1.9890, -0.9076, 1.2569, -1.1703, 0.2675, 0.0012, 0.0839, -0.0265, -0.065};
    private double filterb[] = {0.0864, 0, -0.4320, 0, 0.8640, 0, -0.8640, 0, 0.4320,	0, -0.0864};
    private double output_bank[];
    private double filtTrackArray[][];
    private ArrayList<double[]> filtTrackArrayList;
    private double filtTrackArray2[][];
    private ArrayList<double[]> filtTrackArrayList2;
    private double temp4[];
    private double temp5[];
    private String cam_value;
    public static final String TAG = "Heya";
    private Button button;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void initializeOpenCVDependencies(){
        try{
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_APPEND);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

        // And we are ready to go
        openCvCameraView.enableView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle extras = getIntent().getExtras();

        //openCvCameraView = new JavaCameraView(this, -1);
        setContentView(R.layout.activity_data_collection);
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_surface_view);

        if (extras != null) {
            cam_value = extras.getString("CAMERA_MODE");
        }
        if(cam_value.equals("1")){
            openCvCameraView.setCameraIndex(1);
        }
        else if(cam_value.equals("-1")){
            openCvCameraView.setCameraIndex(-1);
        }
        openCvCameraView.setCvCameraViewListener(this);

        button = (Button) findViewById(R.id.button_capture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "AB entering OnClick ");
                startAnalysis(trackArrayList);
            }
        });

        /*
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        openCvCameraView = new JavaCameraView(this, -1);
        setContentView(openCvCameraView);
        openCvCameraView.setCvCameraViewListener(this);
        */

        /*
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        openCvCameraView = new JavaCameraView(this, -1);
        setContentView(R.layout.activity_data_collection);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cam_value = extras.getString("CAMERA_MODE");
        }
        //openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        if(cam_value.equals("1")){
            openCvCameraView.setCameraIndex(1);
        }
        else if(cam_value.equals("-1")){
            openCvCameraView.setCameraIndex(-1);
        }

        openCvCameraView.setCvCameraViewListener(this);
        */

        /*

          super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cam_value = extras.getString("CAMERA_MODE");
        }
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        if(cam_value.equals("1")){
            mOpenCvCameraView.setCameraIndex(1);
        }
        else if(cam_value.equals("-1")){
            mOpenCvCameraView.setCameraIndex(-1);
        }

        mOpenCvCameraView.setCvCameraViewListener(this);
         */
        //setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu); ---------------------------------------------------------
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

    @Override
    public void onCameraViewStarted(int width, int height) {
        //grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        //grayscaleImage = new Mat(height, width, CvType.CV_8UC1);
        nextImg = new Mat(height, width, CvType.CV_8UC4);
        //tracks = new MatOfPoint2f(200, 90*60*30, CvType.CV_32FC2);
        //tracks = new Mat(200, 90*60*30, CvType.CV_32FC1);
        trackArrayList = new ArrayList<>();
        eigTrackArrayList = new ArrayList<>();
        trackArray = new double[T][];
        eigTrackArray = new double[T][];
        pca = new PrincipalComponentAnalysis();
        //fft = new DoubleFFT_1D(T);
        fft = new DoubleFFT_1D(T/2);
        fftArrayList = new ArrayList<>();
        //In java all elements are initialised to 0 by default.
        output_bank = new double[10];
        filtTrackArray = new double[T][];
        filtTrackArrayList = new ArrayList<>();
        //filtTrackArray2 = new double[T][];
        filtTrackArrayList2 = new ArrayList<>();

        //Mat tracks = new Mat(-1, 2, CvType.CV_32FC(90), new Scalar(0));
        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (int) (height * 0.4);
        //screen_height = (int) (height);
        //screen_width = (int) (width);
    }

    @Override
    public void onCameraViewStopped() {
        Log.d(TAG, "AB Clearing Stuff");
        trackArrayList.clear();
        fftArrayList.clear();
        eigTrackArrayList.clear();
        filtTrackArrayList.clear();
        filtTrackArrayList2.clear();

    }

    @Override
    public Mat onCameraFrame(Mat aInputFrame) {
        //Size sz = new Size(Scalar(100, 100, 100));

        //tracks= Mat.zeros( new Size(200,2), CvType.CV_32FC2);



        frameNo = frameNo + 1;

        if (frameNo > 60) if (FaceDetCount < 5) {
            /********Variable Initialization*********/
            MatOfPoint corners = new MatOfPoint();
            MatOfPoint2f prevPoints_append;
            // Mat nextImg = new Mat(aInputFrame.size(), CvType.CV_8UC1);
            /********Face Detection module starts************/
            Imgproc.cvtColor(aInputFrame, nextImg, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.equalizeHist(nextImg, nextImg);
            MatOfRect faces = new MatOfRect();

            // Use the classifier to detect faces
            if (cascadeClassifier != null) {
                cascadeClassifier.detectMultiScale(nextImg, faces, 1.1, 2, 2,
                        new Size(absoluteFaceSize, absoluteFaceSize), new Size());
            }

            // If there are any faces found, draw a rectangle around it
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                FaceDetCount = FaceDetCount + 1;
                Core.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(),
                        new Scalar(0, 255, 0, 255), 3);

                p1 = new Point(facesArray[i].x + 0.25 * facesArray[i].width,
                        facesArray[i].y + 0.05 * facesArray[i].height);
                p2 = new Point(facesArray[i].x + 0.75 * facesArray[i].width,
                        facesArray[i].y + 0.2 * facesArray[i].height);
                Core.rectangle(aInputFrame, p1, p2,
                        new Scalar(255, 255, 0, 255), 3);

                p3 = new Point(facesArray[i].x + 0.25 * facesArray[i].width,
                        facesArray[i].y + 0.55 * facesArray[i].height);
                p4 = new Point(facesArray[i].x + 0.75 * facesArray[i].width,
                        facesArray[i].y + 0.75 * facesArray[i].height);
                Core.rectangle(aInputFrame, p3, p4,
                        new Scalar(255, 255, 0, 255), 3);
            }
            /********Face Detection module ends************/
            if (FaceDetCount == 5) {

                //Rect rect_up = new Rect(p1.x, p1.y, 0.5*facesArray[0].width,
                //        0.9*facesArray[0].height);
                Rect rect_up = new Rect(p1, p2);
                Mat roi_up = new Mat(nextImg, rect_up);
                // Imgproc.goodFeaturesToTrack(nextImg, corners, 100, 0.01, 5);
                Imgproc.goodFeaturesToTrack(roi_up, corners, Nmax/2, 0.01, 5);
                //corners.convertTo(prevPoints, CvType.CV_32FC2);
                //prevPoints.col(1) = new MatOfPoint2f(corners.toArray());
                prevPoints = new MatOfPoint2f(corners.toArray());
                Point[] prevCornerArray = prevPoints.toArray();
                for (Point p : prevCornerArray) {
                    //Point temp = new Point(p.x+p1.x,p.y+p1.y);
                    //p = temp.clone();
                    //p=p+p1;
                    p.x = p.x + p1.x;
                    p.y = p.y + p1.y;
                }
                prevPoints.fromArray(prevCornerArray);


                Rect rect_down = new Rect(p3, p4);
                Mat roi_down = new Mat(nextImg, rect_down);
                Imgproc.goodFeaturesToTrack(roi_down, corners, Nmax/2, 0.01, 5);
                prevPoints_append = new MatOfPoint2f(corners.toArray());
                Point[] prevCornerArray_append = prevPoints_append.toArray();
                for (Point p : prevCornerArray_append) {
                    //Point temp = new Point(p.x+p1.x,p.y+p1.y);
                    //p = temp.clone();
                    //p=p+p1;
                    p.x = p.x + p3.x;
                    p.y = p.y + p3.y;
                }
                prevPoints_append.fromArray(prevCornerArray_append);

                prevPoints.push_back(prevPoints_append);
                //tracks.add(prevPoints);
                //track_count=track_count+1;
                //CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), '\t');

                N = prevCornerArray.length + prevCornerArray_append.length;
                Log.d(TAG, "AB First points detected");
                prevFrame = aInputFrame;
            }
            //return aInputFrame;

        } else {
            /********Variable Initialization*********/
            Mat prevImg = new Mat(aInputFrame.size(), CvType.CV_8UC4);
            //Mat nextImg = new Mat(aInputFrame.size(), CvType.CV_8UC1);
            //MatOfPoint corners = new MatOfPoint();
            //MatOfPoint2f prevPoints = new MatOfPoint2f();
            MatOfPoint2f nextPoints = new MatOfPoint2f();
            MatOfByte status = new MatOfByte();
            MatOfFloat err = new MatOfFloat();
            double next_time;

            /********Calculating Frame Rate**********/
            if (temp_count<5){
                temp_count = temp_count + 1;
            }else if (temp_count==5){
                prevtime = SystemClock.elapsedRealtime();
                temp_count = temp_count + 1;
            }else if (temp_count==6){
                next_time = SystemClock.elapsedRealtime();
                temp_count = temp_count + 1;
                actual_fps = 1000/(next_time - prevtime);
            }



            Core.rectangle(aInputFrame, p1, p2,
                    new Scalar(255, 255, 0, 255), 3);
            Core.rectangle(aInputFrame, p3, p4,
                    new Scalar(255, 255, 0, 255), 3);

            Imgproc.cvtColor(prevFrame, prevImg, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.cvtColor(aInputFrame, nextImg, Imgproc.COLOR_RGBA2GRAY);
            // Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2GRAY);

            //Imgproc.goodFeaturesToTrack(grayscaleImage, corners, 100, 0.01, 5);
            //corners.convertTo(nextPoints, CvType.CV_32FC2);

            Point[] prevCornerArray = prevPoints.toArray();
            if (prevCornerArray.length > 0) {
                Video.calcOpticalFlowPyrLK(prevImg, nextImg, prevPoints, nextPoints, status, err);
                Log.d(TAG, "AB Other points detected");
            }

            Point[] nextCornerArray = nextPoints.toArray();
            int radius = 2;
            for (Point p : nextCornerArray) {
                Core.circle(aInputFrame, p, radius, new Scalar(0, 0, 255, 255));
            }

            temp = new double[N];

            int i=0;

            if(track_count<T) {
                for (Point p : prevCornerArray) {
                    temp[i]=p.y;
                    //trackArray[track_count][i]=p.y;
                    i=i+1;
                    //p.x = p.x + p1.x;
                    //p.y = p.y + p1.y;
                }
                //prevPoints.fromArray(prevCornerArray);
                //tracks.col(track_count).fromArray(temp);
                //tracks.fromArray(temp);
                trackArrayList.add(temp);
                //trackArray = trackArrayList.toArray(trackArray);
                track_count = track_count + 1;
            }else {
                FLAG = 1;
                //startAnalysis(trackArrayList);
                //Mat eigenVectors = new Mat();
                //Mat mean = new Mat();
                //Mat trackArrayMat = new Mat();
                //trackArrayMat.fromArray(trackArray);
                //trackArrayMat = Mat(T, N, CvType.CV_32FC1, &trackArray[0][0]);
                //trackArrayMat = Mat(trackArray);
                //Core.PCACompute(trackArrayMat, mean, eigenVectors);

            }
            prevPoints = nextPoints;
            prevFrame = aInputFrame;

            //if(track_count<90*60*30) {
            //    tracks.add(prevPoints);
            //    track_count = track_count + 1;
            //}else{
            //    FLAG=1;
            //}

            //List<MatOfPoint2f> tracks = new Vector<MatOfPoint2f>();
            //imageMat.add(image1);
            //imageMat.add(image2);

            //return aInputFrame;
        }

        return aInputFrame;
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
    /*
    public void stopThread(){
        if(DataCollection.getThread()!=null){
            DataCollection.getThread().interrupt();
            DataCollection.setThread(null);
        }
    }
    */


    /***** Called when we have enough data to calculate heartrate ******/
    public void startAnalysis(ArrayList<double[]> trackArrayList) {
        if (track_count == T) {
            /************* Data Analysis Starts *********************/
            trackArray = trackArrayList.toArray(trackArray);
            double temp3;
            //temp4 = new double[T];
            int ind;
            for (int p = 0; p < N; p = p + 1) {
                temp4 = new double[T];
                //temp4 = null;
                //temp4 = [];
                //Arrays.fill(temp4, 0);
                for (int q = 0; q < T; q = q + 1) {
                    temp3 = 0.0;

                    //trackArray_filt[q][p]=new double[1][1];
                    for (int r = 9; r >= 0; r = r - 1) {
                        temp3 = temp3 - filtera[9 - r] * output_bank[r]; //output bank ordered as immediate previous sample last

                    }
                    //temp3 = -temp3;
                    if (q <= 10) {
                        for (int s = 0; s <= q; s = s + 1) {
                            temp3 = temp3 + filterb[s] * trackArray[q - s][p];
                        }

                    } else {
                        for (int s = 0; s <= 10; s = s + 1) {
                            //temp3 = temp3 + filterb[s] * trackArray[(q / 10) * 10 + s][p];
                            temp3 = temp3 + filterb[s] * trackArray[q - s][p];
                        }
                    }

                    //trackArray_filt[q][p] = temp3;
                    temp4[q] = temp3;
                    //ind = 9- q % 10;

                    for (ind = 1; ind <= 9; ind = ind + 1) {
                        output_bank[ind - 1] = output_bank[ind];

                    }
                    output_bank[9] = temp3;
                }
                filtTrackArrayList.add(temp4);
                Arrays.fill(output_bank, 0);
                //Arrays.fill(temp4, 0);
                //output_bank = null;
                //output_bank = [];
            }

            //trackArrayList = filtTrackArrayList;
            FLAG = 1;
            //filtTrackArrayList = transpose(filtTrackArrayList);

            filtTrackArray = filtTrackArrayList.toArray(filtTrackArray);
            filtTrackArray2 = new double[T][N];
            //Arrays.fill(filtTrackArray2, 0.0);
            for (int m = 0; m < T; m = m + 1) {
                for (int l = 0; l < N; l = l + 1) {
                    //Arrays.fill(filtTrackArray2[m], 0.0);
                    filtTrackArray2[m][l] = filtTrackArray[l][m];
                    FLAG = 0;
                }
                FLAG = 0;
            }

            //temp5 = new double[N];
            for (int l = 0; l < T; l = l + 1) {
                temp5 = new double[N];
                for (int m = 0; m < N; m = m + 1) {
                    temp5[m] = filtTrackArray2[l][m];
                }
                //filtTrackArrayList2.add(filtTrackArray2[T][]);
                filtTrackArrayList2.add(temp5);
            }


            /**************PCA Block**************/


            pca.setup(T, N);
            for (double[] sample : filtTrackArrayList2) {
                pca.addSample(sample);
                FLAG = 0;
            }
            //filtTrackArrayList2.clear();
            FLAG = 0;
            pca.computeBasis(5); // int numComponents
            FLAG = 1;
            temp = new double[5];
            for (double[] sample : trackArrayList) {
                temp = pca.sampleToEigenSpace(sample);
                eigTrackArrayList.add(temp);
            }
            FLAG = 0;
            /**************PCA Block ends**************/
            //fftArrayList = eigTrackArrayList;
            // DoubleFFT_1D fft = new DoubleFFT_1D(T/2);
            //double[] temp2 = new double[T];
            eigTrackArray = eigTrackArrayList.toArray(eigTrackArray);
            for (int j = 0; j < 5; j = j + 1) {
                double[] temp2 = new double[T];
                for (int m = 0; m < T; m = m + 1) {
                    temp2[m] = eigTrackArray[m][j];
                    FLAG = 0;
                    //fft.complexForward(eigTrackArray[:][i]);
                }
                //fft.complexForward(temp2);
                fft.realForward(temp2);
                fftArrayList.add(temp2);
            }
            //fft.complexForward(a);
            FLAG = 1;
            /********** Signal Selection *************/
            double maxP, sumP, tempP, ratioP;
            double maxratioP = 0.01;
            int z = 0;
            int maxz = 0;
            //double ratioP[] = new double[5];
            temp = new double[T];
            for (double[] traject : fftArrayList) {
                maxP = traject[2] * traject[2] + traject[3] * traject[3];
                sumP = maxP;
                for (int p = 4; p < T - 1; p = p + 2) {
                    tempP = traject[p] * traject[p] + traject[p + 1] * traject[p + 1];
                    sumP = sumP + tempP;
                    if (tempP > maxP) {
                        maxP = tempP;
                    }
                }
                //ratioP[z] = maxP/sumP;
                //z = z+1;
                ratioP = maxP / sumP;
                if (ratioP > maxratioP) {
                    maxz = z;
                    maxratioP = ratioP;
                }

                z = z + 1;
            }
            FLAG = 1;
            /*** Now we know which trajectory to calculate heartbeat with --> maxz**/
            /************** Heartbeat Calculation **************/
            double peaktraj[] = new double[T];
            for (int p = 0; p < T; p = p + 1) {
                peaktraj[p] = eigTrackArray[p][maxz];
            }

            double tempD, sumDistPeak = 0.0001;
            int countPeak = 0;
            int lastPeak = 0;
            for (int p = 2; p < T - 2; p = p + 1) {
                tempD = 0.0;
                //&& (peaktraj[p]>peaktraj[p+2]) && (peaktraj[p] > peaktraj[p-2])
                if ((peaktraj[p] > peaktraj[p+1]) && (peaktraj[p] > peaktraj[p-1])&& (peaktraj[p]>peaktraj[p+2]) && (peaktraj[p] > peaktraj[p-2])){
                    tempD = 1.0;
                }

                //tempD = (7.995*peaktraj[p]) -(4*peaktraj[p - 1]) - (4*peaktraj[p + 1]);
                if (tempD > 0.0) {
                    countPeak = countPeak + 1;
                    //sumDistPeak = sumDistPeak + tempD
                    if (countPeak == 1) {
                        lastPeak = p;
                    } else {
                        sumDistPeak = sumDistPeak + (p - lastPeak);
                        lastPeak = p;
                    }
                }
            }

            double heartbeat = actual_fps * 60 * countPeak / sumDistPeak;
            FLAG = 0;


        /*trackArrayList.clear();
        fftArrayList.clear();
        eigTrackArrayList.clear();
        filtTrackArrayList.clear();
        filtTrackArrayList2.clear();
*/
            Intent intent = new Intent(this, DataAnalysis.class);
            intent.putExtra("MESSAGE", heartbeat);
            //intent.putSeria("MESSAGE", heartbeat );
            startActivity(intent);

        }
    }
}
