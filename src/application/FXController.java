package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import utils.Utils;

public class FXController {
	private final String VPTS_KEY = "vesselKey";
	private final String VCP1_KEY = "vesselCirclept1Key";
	private final String VCP2_KEY = "vesselCirclept2Key";
	private final String VSPTS_KEY = "vesselScaleKey";
	private final String VSP1_KEY = "vesselScalept1Key";
	private final String VSP2_KEY = "vesselScalept2Key";
	private final String DPTS_KEY = "doppKey";
	private final String DCP1_KEY = "dopCirpt1Key";
	private final String DCP2_KEY = "dopCirpt2Key";
	private final String ZA_KEY = "ZeroAxisKey";
	
	private final String[] SETUP_KEYS = new String[]{VPTS_KEY, VCP1_KEY, VCP2_KEY, VSPTS_KEY, VSP1_KEY, VSP2_KEY, DPTS_KEY, DCP1_KEY, DCP2_KEY, ZA_KEY};

	@FXML
	private Button btnDec13;
	@FXML
	private Button btnDec14;
	@FXML
	private TextField txtDopZeroAxis;
	@FXML
	private Button btnSetInnerVesselLine1;
	@FXML
	private Button btnDec10;
	@FXML
	private TextField txtDopVScale;
	@FXML
	private Button btnDec11;
	@FXML
	private Button btnDec12;
	@FXML
	private TextField txtScaley1;
	@FXML
	private TextField txtScaley2;
	@FXML
	private Label lblPxltoCm;
	@FXML
	private Text lblTotalFrames;
	@FXML
	private TextField txtVessel1x;
	@FXML
	private TextField txtVessel1y;
	@FXML
	private TextField txtDopUpperLimit;
	@FXML
	private Button btnDec1;
	@FXML
	private Button btnDec5;
	@FXML
	private Button btnDec4;
	@FXML
	private Button btnDec3;
	@FXML
	private Button btnDec2;
	@FXML
	private Button btnSetInnerVesselLine;
	@FXML
	private Button btnSetPxlScaleCm;
	@FXML
	private Button btnDec7;
	@FXML
	private Button btnDec6;
	@FXML
	private TextField txtVessel2y;
	@FXML
	private TextField txtVessel2x;
	@FXML
	private Button btnInc1;
	@FXML
	private TextField txtDopRightLimit;
	@FXML
	private Label lblHDopScale;
	@FXML
	private TextField txtVesselxScale;
	@FXML
	private TextField txtDopLeftLimit;
	@FXML
	private Button btnInc7;
	@FXML
	private Button btnStartAnalysis;
	@FXML
	private Button btnInc6;
	@FXML
	private Button btnInc3;
	@FXML
	private Button btnInc2;
	@FXML
	private Button btnInc5;
	@FXML
	private Button btnInc4;
	@FXML
	private TextField txtVesselScale;
	@FXML
	private ImageView currentFrame;
	@FXML
	private Label txtScalex;
	@FXML
	private Button btnPrevFrame;
	@FXML
	private MenuItem btnLoadVid;
	@FXML
	private Button btnInc12;
	@FXML
	private Button btnInc11;
	@FXML
	private Button btnNextFrame;
	@FXML
	private Button btnInc10;
	@FXML
	private Button btnInc14;
	@FXML
	private TextField txtDopLowerLimit;
	@FXML
	private Button btnInc13;
	@FXML
	private TextField txtDopHScale;
	@FXML
	private Label lblVDopScale;
	@FXML
	private TextField txtfCurrFrame;
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;

	private ArrayList<Mat> matArray;
	private ArrayList<Mat> srcMatArray;
	private ArrayList<Double> diaArray = new ArrayList<Double>();
	private ArrayList<Double> uDopArray = new ArrayList<Double>();
	private ArrayList<Double> lDopArray = new ArrayList<Double>();
	private ArrayList<ArrayList<DrawParams>> linesArray = new ArrayList<>();
	private HashMap<String, DrawParams> setUpLinesMap = new HashMap<>();
	private int currIndex=0, totalIndex=0;

	private int mode = -1;
	private boolean readyToUpdate = true;
	@FXML
	void startAnalysis(ActionEvent event) {
		setMode(-1);
		System.out.println("analisis");
		if(vesselpt1!=null&&vesselpt2!=null) {
			if(vesselScalept1!=null&&vesselScalept2!=null) {
				if(dopplerpt1!=null&&dopplerpt2!=null) {
					System.out.println("Hope This works...");
					uDopArray = new ArrayList<Double>();
					lDopArray = new ArrayList<Double>();
					int dx1 = (int) (dopplerpt1.x<dopplerpt2.x?dopplerpt1.x:dopplerpt2.x);
					int dx2 = (int) (dopplerpt1.x>dopplerpt2.x?dopplerpt1.x:dopplerpt2.x);
					int dy1 = (int) (dopplerpt1.y<dopplerpt2.y?dopplerpt1.y:dopplerpt2.y);
					int dy2 = (int) (dopplerpt1.y>dopplerpt2.y?dopplerpt1.y:dopplerpt2.y);
					zeroAxis = getZeroAxis(srcMatArray.get(0).colRange(dx1, dx2).rowRange(dy1,  dy2));

					int vx1 = (int) (vesselpt1.x<vesselpt2.x?vesselpt1.x:vesselpt2.x);
					int vx2 = (int) (vesselpt1.x>vesselpt2.x?vesselpt1.x:vesselpt2.x);
					int vy1 = (int) (vesselpt1.y<vesselpt2.y?vesselpt1.y:vesselpt2.y);
					int vy2 = (int) (vesselpt1.y>vesselpt2.y?vesselpt1.y:vesselpt2.y);
					diaArray = new ArrayList<Double>();
					ArrayList<int[]> lineArr = getLineArray(vesselpt1, vesselpt2);

					linesArray = new ArrayList<>();
					for(int i=0;i<matArray.size();i++) {
						linesArray.add(new ArrayList<DrawParams>());
						analyzeFrameDoppler(i, dx1, dy1, dx2, dy2);
						analyzeFrameVessel(i, vx1, vy1, vx2, vy2, lineArr);
						setFrameByIndex(i);
					}
					System.gc();

					System.out.println("...Did it work?");
				}
				else System.out.println("set doppler");
			}
			else System.out.println("set scale");
		}
		else System.out.println("set vessel");
	}

	//	public void updateProgressUI(int index) {
	//		readyToUpdate = false;
	//		new Thread(() -> {
	//			Platform.runLater(() -> {
	//				setFrameByIndex(index);
	//				readyToUpdate=true;
	//			});// Update on JavaFX Application Thread
	//		}).start();
	//	}

	private void drawLinesForFrame(int frame) {
		//		if(frame<linesArray.size()) {
		//			ArrayList<DrawParams> lines = linesArray.get(frame);
		//			for(DrawParams l: lines) 
		//				Imgproc.line(matArray.get(frame), l.p1, l.p2, l.color, l.thickness);
		//		}
		matArray.set(frame,	srcMatArray.get(frame).clone());
		for(String key : SETUP_KEYS) {
			DrawParams dp = setUpLinesMap.get(key);
			if(dp!=null) {
				if(key==DPTS_KEY) {
					Imgproc.rectangle(matArray.get(frame), dp.p1, dp.p2, dp.color, dp.thickness);
				}
				else if(dp.isCircle==true) {
					Imgproc.circle(matArray.get(frame), dp.p1, dp.radius, dp.color, dp.thickness);
				}
				else {
					Imgproc.line(matArray.get(frame), dp.p1, dp.p2, dp.color, dp.thickness);
				}
			}
		}

		if(frame<linesArray.size()) {
			ArrayList<DrawParams> lines = linesArray.get(frame);
			for(DrawParams l: lines) { 
				Imgproc.line(matArray.get(frame), l.p1, l.p2, l.color, l.thickness);
			}
		}
	}

	private int lastTime = 0;
	private double numErrs = 0;
	private int zeroAxis = 0;
	private void analyzeFrameDoppler(int frame, int x1, int y1, int x2, int y2) {
		Point pt1 = new Point(x1, y1+zeroAxis);
		Point pt2 = new Point(x2, y1+zeroAxis);
		Mat m = srcMatArray.get(frame).colRange(x1, x2).rowRange(y1,  y2);
		Mat dest = matArray.get(frame);
		linesArray.get(frame).add(new DrawParams(pt1, pt2, new Scalar(0,0,255), 1));
		analyzeULDoppler(dest, m, zeroAxis-y1, frame, x1, y1);
		matArray.set(frame, dest);
		m = null;
		dest = null;
	}

	private int getZeroAxis(Mat m) {
		int zeroAxis = -1;
		double whitestLineSum = 0;
		Mat gmat = new Mat();
		Imgproc.Canny(m, gmat, 20, 170);
		Imgproc.cvtColor(gmat, gmat, Imgproc.COLOR_GRAY2BGR);
		for(int y=0;y<m.height();y++){
			double lineSum = 0;
			for(int x=0;x<m.width();x++) {
				double[] pVal = m.get(y, x);
				lineSum += (pVal[0]+pVal[1]+pVal[2])/3;
			}
			if(whitestLineSum<lineSum) {
				whitestLineSum=lineSum;
				zeroAxis = y;
			}
		}
		return zeroAxis;
	}

	private Mat analyzeULDoppler(Mat dest, Mat m, int mZeroAxis, int frame, int xOffsetglb, int yOffsetglb){
		boolean foundCurr = false;
		int currX = lastTime, mWidth = m.width(), mHeight = m.height(), zeroLimit = 10, c = 0;
		while(!foundCurr && ++currX!=lastTime&&c++<mWidth) {
			currX %= mWidth;
			double pixelSum = 0;
			for(int i=0;i<mHeight;i++) {
				if(Math.abs(mZeroAxis-i)<=zeroLimit) continue;
				double[] pVal = m.get(i, currX);
				pixelSum += (pVal[0]+pVal[1]+pVal[2])/3;
			}
			if(pixelSum/mHeight<=5) {
				foundCurr = true;
			}
		}
		if(!foundCurr) {
			numErrs++;
			return dest;
		}
		lastTime = currX;
		if(frame==0) {
			uDopArray.add((double) -1);
			lDopArray.add((double) -1);
			return dest;
		}
		else {
			//			int numPointsbetwn = 4;
			//			int xDiff = currX-lastX;
			//			if(xDiff<0)xDiff+=mWidth;
			//			double step = ((double)xDiff)/numPointsbetwn;
			//			for(int x=0;x<numPointsbetwn||x<xDiff;x++) {
			//				int xOffset = (int) Math.round(x*step);
			int xOffset = 5;
			double maxPixelVal = 0, pSum = 0;
			int maxPixelValIndex = mZeroAxis, div = 0;
			for(int i=0;i<mHeight;i++) {
				if(Math.abs(mZeroAxis-i)<=1)continue;
				int xPt = currX-xOffset;
				if(xPt<0)xPt+=mWidth;
				double[] p = m.get(i, xPt);
				double pVal = (p[0]+p[1]+p[2])/3;
				if(pVal>0) {
					div++;
					pSum+=pVal;
				}
				pSum += pVal;
				if(pVal>maxPixelVal) {
					maxPixelValIndex = i;
					maxPixelVal = pVal;
				}
			}
			if(div>0) {
				pSum/=div;
				boolean lpFound = false, upFound = false;
				int lp = maxPixelValIndex, up = maxPixelValIndex;
				int li = maxPixelValIndex, ui = maxPixelValIndex;
				int medValThreshold = 10;
				Point dot1;
				Point dot2;
				for(;!(lpFound&&upFound)&&(li<mHeight&&up>0);li++, ui--) {
					int xPt = currX-xOffset;
					if(xPt<0)xPt+=mWidth;
					double[] pu = m.get(ui, xPt);
					double[] pl = m.get(li, xPt);
					double puVal = (pu[0]+pu[1]+pu[2])/3;
					double plVal = (pl[0]+pl[1]+pl[2])/3;
					if(!upFound || Math.abs(mZeroAxis-up)>1) {
						if(puVal<(pSum/2)) {
							upFound = true;
							up = ui-1;
							//							dot1 = new Point(currX-8, ui+1);
							//							dot2 = new Point(currX-2, ui+1);
							//							Imgproc.line(dest, dot1, dot2, new Scalar(0,0,255), 2);
						}else if(puVal<((3*pSum)/4)) {
							if(medValThreshold<1) {
								upFound = true;
								up = ui-1;
								dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+ui+1);
								dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+ui+1);
								linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(0,0,255), 2));
							}
							else {
								medValThreshold--;
								dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+ui);
								dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+ui);
								linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(255,0,0), 1));
							}
						}else {
							dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+ui);
							dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+ui);
							linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(0,255,0), 1));
						}
					}

					if(!lpFound || Math.abs(mZeroAxis-lp)>1) {
						if(plVal<(pSum/2)) {
							lpFound = true;
							lp = li-1;
							//							dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+li-1);
							//							dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+li-1);
							//							Imgproc.line(dest, dot1, dot2, new Scalar(0,0,255), 2);
						}else if(plVal<((3*pSum)/4)) {
							if(medValThreshold<1) {
								lpFound = true;
								lp = li-1;
								dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+li-1);
								dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+li-1);
								linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(0,0,255), 2));
							}
							else {
								medValThreshold--;
								dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+li);
								dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+li);
								linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(255,0,0), 1));
							}
						}else {
							dot1 = new Point(xOffsetglb+currX-8, yOffsetglb+lp);
							dot2 = new Point(xOffsetglb+currX-2, yOffsetglb+lp);
							linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(0,255,0), 1));
						}
					}
				}
				uDopArray.add((double) up);
				lDopArray.add((double) lp);
			}
			else {
				uDopArray.add((double) -1);
				lDopArray.add((double) -1);
			}
			Point pt1 = new Point(xOffsetglb+currX, yOffsetglb);
			Point pt2 = new Point(xOffsetglb+currX, yOffsetglb+mHeight-1);
			linesArray.get(frame).add(new DrawParams(pt1, pt2, new Scalar(0,255,255), 2));
			Point dot1 = new Point(xOffsetglb, yOffsetglb+maxPixelValIndex);
			Point dot2 = new Point(xOffsetglb+mWidth, yOffsetglb+maxPixelValIndex);
			linesArray.get(frame).add(new DrawParams(dot1, dot2, new Scalar(255,255,255), 2));
			//			}
			return dest;
		}
	}

	private void analyzeFrameVessel(int frame, int vx1, int vy1, int vx2, int vy2, ArrayList<int[]> lineArr) {
		Mat m = srcMatArray.get(frame).colRange(vx1-20, vx2+20).rowRange(vy1-200>0?vy1-200:0,  vy2+200);
		Mat gmat = new Mat();
		Mat dest = matArray.get(frame);
		Imgproc.Canny(m, gmat, 50, 170);
		Imgproc.cvtColor(gmat, gmat, Imgproc.COLOR_GRAY2BGR);
//		linesArray.get(frame).add(new DrawParams(vesselpt1, vesselpt2, new Scalar(0,0,255), 2));
		dest = analyzeWalls(dest, gmat, lineArr, frame, vx1-20, vy1-200>0?vy1-200:0);
		matArray.set(frame, dest);
	}

	private Mat analyzeWalls(Mat dest, Mat medgeMat, ArrayList<int[]> lineArr, int frame, int xOffset, int yOffset){
		Mat ret = dest;
		double avgDiameter = 0;
		int l = 0;
		for(int[] ia:lineArr) {
			boolean upperFound = false;
			boolean lowerFound = false;
			int lp = -1;
			for(int y=ia[1];y<medgeMat.rows()&&!lowerFound;y++) {//LowerWall
				double[] p = medgeMat.get(y-yOffset, ia[0]-xOffset);
				if(p[0]==255&&p[1]==255&&p[2]==255) {
					lowerFound = true;
					Point dot = new Point(ia[0], y);
					linesArray.get(frame).add(new DrawParams(dot, dot, new Scalar(0,255,255), 2));
					lp = y;
				}
			}
			int up = -1;
			for(int y=ia[1];y>0&&!upperFound;y--) {//UpperWall
				double[] p = medgeMat.get(y-yOffset, ia[0]-xOffset);
				if(p[0]==255&&p[1]==255&&p[2]==255) {
					upperFound = true;
					Point dot = new Point(ia[0], y);
					linesArray.get(frame).add(new DrawParams(dot, dot, new Scalar(255,255,0), 2));
					up = y;
				}
			}
			if(lp>=0 && up>=0) {
				avgDiameter += lp - up;
				l++;
			}
		}
		if(l>0)avgDiameter /= l;
		diaArray.add(new Double(avgDiameter));
		return ret;
	}

	private ArrayList<int[]> getLineArray(Point pt1, Point pt2){
		return getLineArray(pt1.x, pt1.y, pt2.x, pt2.y);
	}

	private ArrayList<int[]> getLineArray(double x0, double y0, double x1, double y1){
		ArrayList<int[]> line = new ArrayList<int[]>();
		double deltax = x1 - x0;
		double deltay = y1 - y0;
		double deltaerr = Math.abs(deltay / deltax);    // Assume deltax != 0 (line is not vertical),
		// note that this division needs to be done in a way that preserves the fractional part
		double error = 0.0; // No error at start
		int y = (int)y0;
		for(int x=(int)x0;x<=x1;x++) { 
			line.add(new int[] {x,y});
			error = error + deltaerr;
			while(error>=0.5) {
				if(deltay!=0) y = deltay>0?y+1:y-1;
				error = error - 1.0;
			}
		}
		return line;
	}
	private Size frameSize;
	@FXML
	void loadVideo() {
		loadVideoByFileName("RyanBrachialStarTechToStreamCatcher.MP4");
	}
	private void loadVideoByFileName(String fileName) {
		System.out.println("start");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String url=fileName;

		VideoCapture videoCapture=new VideoCapture(url);
		frameSize=new Size((int)videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
		System.out.println(videoCapture);
		System.out.println(frameSize);
		final FourCC fourCC=new FourCC("XVID");

		matArray = new ArrayList<Mat>();
		srcMatArray = new ArrayList<Mat>();
		final Mat mat=new Mat();
		int frames=0;
		final long startTime=System.currentTimeMillis();
		while (videoCapture.read(mat)) {
			Mat m  = new Mat();
			Imgproc.cvtColor(mat, m, Imgproc.COLOR_RGB2BGR);
			matArray.add(m);
			srcMatArray.add(m.clone());
			totalIndex++;
		}
		final long estimatedTime=System.currentTimeMillis() - startTime;
		System.out.println("\n"+((double)estimatedTime/1000)+" seconds");
		System.out.println("DOEN");
		lblTotalFrames.setText(lblTotalFrames.getText()+":\t"+totalIndex);
		setFrameByIndex(0);
	}

	@FXML
	void previousFrame(ActionEvent event) {
		//		System.out.println("prev");
		if(currIndex>0) {
			setFrameByIndex(--currIndex);
		}
	}

	@FXML
	void nextFrame(ActionEvent event) {
		//		System.out.println("next");
		if(totalIndex>0) {
			setFrameByIndex(++currIndex);
		}
	}

	@FXML
	void initialize() {
		assertion();
		setImagViewEvents();
		setTextListeners();
		setButtonListeners();
	}

	private void assertion() {
		assert btnDec13 != null : "fx:id=\"btnDec13\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec14 != null : "fx:id=\"btnDec14\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopZeroAxis != null : "fx:id=\"txtDopZeroAxis\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnSetInnerVesselLine1 != null : "fx:id=\"btnSetInnerVesselLine1\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec10 != null : "fx:id=\"btnDec10\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopVScale != null : "fx:id=\"txtDopVScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec11 != null : "fx:id=\"btnDec11\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec12 != null : "fx:id=\"btnDec12\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtScaley1 != null : "fx:id=\"txtScaley1\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtScaley2 != null : "fx:id=\"txtScaley2\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert lblPxltoCm != null : "fx:id=\"lblPxltoCm\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert lblTotalFrames != null : "fx:id=\"lblTotalFrames\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVessel1x != null : "fx:id=\"txtVessek1x\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVessel1y != null : "fx:id=\"txtVessek1y\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopUpperLimit != null : "fx:id=\"txtDopUpperLimit\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec1 != null : "fx:id=\"btnDec1\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec5 != null : "fx:id=\"btnDec5\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec4 != null : "fx:id=\"btnDec4\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec3 != null : "fx:id=\"btnDec3\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec2 != null : "fx:id=\"btnDec2\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnSetInnerVesselLine != null : "fx:id=\"btnSetInnerVesselLine\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnSetPxlScaleCm != null : "fx:id=\"btnSetPxlScaleCm\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec7 != null : "fx:id=\"btnDec7\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnDec6 != null : "fx:id=\"btnDec6\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVessel2y != null : "fx:id=\"txtVessek2y\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVessel2x != null : "fx:id=\"txtVessek2x\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc1 != null : "fx:id=\"btnInc1\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopRightLimit != null : "fx:id=\"txtDopRightLimit\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert lblHDopScale != null : "fx:id=\"lblHDopScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVesselxScale != null : "fx:id=\"txtVessekScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopLeftLimit != null : "fx:id=\"txtDopLeftLimit\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc7 != null : "fx:id=\"btnInc7\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnStartAnalysis != null : "fx:id=\"btnStartAnalysis\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc6 != null : "fx:id=\"btnInc6\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc3 != null : "fx:id=\"btnInc3\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc2 != null : "fx:id=\"btnInc2\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc5 != null : "fx:id=\"btnInc5\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc4 != null : "fx:id=\"btnInc4\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtVesselScale != null : "fx:id=\"txtVesselScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert currentFrame != null : "fx:id=\"currentFrame\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtScalex != null : "fx:id=\"txtScalex\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnPrevFrame != null : "fx:id=\"btnPrevFrame\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnLoadVid != null : "fx:id=\"btnLoadVid\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc12 != null : "fx:id=\"btnInc12\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc11 != null : "fx:id=\"btnInc11\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnNextFrame != null : "fx:id=\"btnNextFrame\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc10 != null : "fx:id=\"btnInc10\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc14 != null : "fx:id=\"btnInc14\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopLowerLimit != null : "fx:id=\"txtDopLowerLimit\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert btnInc13 != null : "fx:id=\"btnInc13\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtDopHScale != null : "fx:id=\"txtDopHScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert lblVDopScale != null : "fx:id=\"lblVDopScale\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
		assert txtfCurrFrame != null : "fx:id=\"txtfCurrFrame\" was not injected: check your FXML file 'HeartByteJFX.fxml'.";
	}

	
	private void setMode(int m) {
		mode = m;
	}

	@FXML
	void setModeToIVL(ActionEvent event) {
		vesselpt1=null;
		vesselpt2=null;
		setUpLinesMap.remove(VPTS_KEY);
		setUpLinesMap.remove(VCP1_KEY);
		setUpLinesMap.remove(VCP2_KEY);
		setFrameByIndex(currIndex);
		setMode(0);
		System.out.println(mode);
	}

	@FXML
	void setModeToVPS(ActionEvent event) {
		vesselScalept1=null;
		vesselScalept2=null;
		setUpLinesMap.remove(VSPTS_KEY);
		setUpLinesMap.remove(VSP1_KEY);
		setUpLinesMap.remove(VSP2_KEY);
		setFrameByIndex(currIndex);
		setMode(1);
		System.out.println(mode);
	}

	@FXML
	void setModeToD(ActionEvent event) {
		dopplerpt1=null;
		dopplerpt2=null;
		setUpLinesMap.remove(DPTS_KEY);
		setUpLinesMap.remove(DCP1_KEY);
		setUpLinesMap.remove(DCP2_KEY);
		setFrameByIndex(currIndex);
		setMode(2);
		System.out.println(mode);
	}

	private void setImagViewEvents() {
		currentFrame.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.out.println("View: X-"+e.getX()+"\tY-"+e.getY());
				switch(mode) {
				case 0:
					setVesselPoints(e.getX(), e.getY());
					break;
				case 1:
					setVesselPixelScale(e.getX(), e.getY());
					break;
				case 2:
					setDopplerPoints(e.getX(), e.getY());
					break;
				default:
					System.out.println("Def Block");
					break;
				}
			}
		});
	}

	private void setTextListeners() {
		txtVessel1x.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtVessel1x.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtVessel1x.setText("");
				}
				else if(vesselpt1!=null){
					vesselpt1.x = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtVessel1y.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtVessel1y.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtVessel1y.setText("");
				}
				else if(vesselpt1!=null){
					vesselpt1.y = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtVessel2x.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtVessel2x.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtVessel2x.setText("");
				}
				else if(vesselpt2!=null){
					vesselpt2.x = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtVessel2y.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				System.out.println("written");
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtVessel2y.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtVessel2y.setText("");
				}
				else if(vesselpt2!=null){
					vesselpt2.y = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtVesselScale.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("^\\d*\\.?\\d*$")) {
					txtVesselScale.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					lblPxltoCm.setText("");
				}
				else if(vesselScalept1!=null&&vesselScalept2!=null) {
					pxlToCm = Math.abs((vesselScalept1.y - vesselScalept2.y))/Double.parseDouble(val);
					lblPxltoCm.setText(Double.toString(pxlToCm));
				}
			}
		});
		txtScaley1.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtScaley1.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtScaley1.setText("");
				}
				else if(vesselScalept1!=null){
					System.out.println(vesselScalept1.y);
					vesselScalept1.y = Integer.parseInt(val);
					System.out.println(vesselScalept1.y);
					setUpLinesMap.put(VSP1_KEY, new DrawParams(new Point(vesselScalept1.x-10, vesselScalept1.y), new Point(vesselScalept1.x+10, vesselScalept1.y), new Scalar(0, 255, 0), 2));
					setUpLinesMap.put(VSP2_KEY, new DrawParams(new Point(vesselScalept2.x-10, vesselScalept2.y), new Point(vesselScalept2.x+10, vesselScalept2.y), new Scalar(0, 255, 0), 2));
				}
				if(!vsInit)
				setFrameByIndex(currIndex);
			}
		});
		txtScaley2.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtScaley2.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtScaley2.setText("");
				}
				else if(vesselScalept2!=null){
					System.out.println(vesselScalept2.y);
					vesselScalept1.y = Integer.parseInt(val);
					System.out.println(vesselScalept2.y);
					setUpLinesMap.put(VSP1_KEY, new DrawParams(new Point(vesselScalept1.x-10, vesselScalept1.y), new Point(vesselScalept1.x+10, vesselScalept1.y), new Scalar(0, 255, 0), 2));
					setUpLinesMap.put(VSP2_KEY, new DrawParams(new Point(vesselScalept2.x-10, vesselScalept2.y), new Point(vesselScalept2.x+10, vesselScalept2.y), new Scalar(0, 255, 0), 2));
				}
				if(!vsInit)
				setFrameByIndex(currIndex);
			}
		});
		txtVesselxScale.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtVesselxScale.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtVesselxScale.setText("");
				}
				else if(vesselScalept1!=null&&vesselScalept2!=null){
					vesselScalept1.x = Integer.parseInt(val);
					vesselScalept2.x = Integer.parseInt(val);
					setUpLinesMap.put(VSP1_KEY, new DrawParams(new Point(vesselScalept1.x-10, vesselScalept1.y), new Point(vesselScalept1.x+10, vesselScalept1.y), new Scalar(0, 255, 0), 2));
					setUpLinesMap.put(VSP2_KEY, new DrawParams(new Point(vesselScalept2.x-10, vesselScalept2.y), new Point(vesselScalept2.x+10, vesselScalept2.y), new Scalar(0, 255, 0), 2));
				}
				if(!vsInit)
				setFrameByIndex(currIndex);
			}
		});
		txtDopLeftLimit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtDopLeftLimit.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtDopLeftLimit.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null){
					if(dopplerpt1.x<dopplerpt2.x)
						dopplerpt1.x = Integer.parseInt(val);
					else
						dopplerpt2.x = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtDopRightLimit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtDopRightLimit.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtDopRightLimit.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null){
					if(dopplerpt1.x>dopplerpt2.x)
						dopplerpt1.x = Integer.parseInt(val);
					else
						dopplerpt2.x = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtDopUpperLimit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtDopUpperLimit.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtDopUpperLimit.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null){
					if(dopplerpt1.y<dopplerpt2.y)
						dopplerpt1.y = Integer.parseInt(val);
					else
						dopplerpt2.y = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtDopLowerLimit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("\\d*")) {
					txtDopLowerLimit.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					txtDopLowerLimit.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null){
					if(dopplerpt1.y>dopplerpt2.y)
						dopplerpt1.y = Integer.parseInt(val);
					else
						dopplerpt2.y = Integer.parseInt(val);
				}
				setFrameByIndex(currIndex);
			}
		});
		txtDopHScale.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("^\\d*\\.?\\d*$")) {
					txtDopHScale.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					lblHDopScale.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null) {
					dopHScaleVal = Math.abs((dopplerpt1.x - dopplerpt2.x))/Double.parseDouble(val);
					lblHDopScale.setText(Double.toString(dopHScaleVal));
				}
			}
		});
		txtDopVScale.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				if (!newValue.matches("^\\d*\\.?\\d*$")) {
					txtDopVScale.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					lblVDopScale.setText("");
				}
				else if(dopplerpt1!=null&&dopplerpt2!=null) {
					dopVScaleVal = Math.abs((dopplerpt1.y - dopplerpt2.y))/Double.parseDouble(val);
					lblVDopScale.setText(Double.toString(dopVScaleVal));
				}
			}
		});
		txtfCurrFrame.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				String val = oldValue;
				System.out.println(newValue+"\t"+!newValue.matches("\\d*"));
				if (!newValue.matches("\\d*")) {
					txtfCurrFrame.setText(oldValue);
				}
				else val = newValue;
				if(val.length()==0) {
					System.out.println("hit1"+val+"\t"+newValue);
					currIndex = 0; 
					txtfCurrFrame.setText("1");
					setFrameByIndex(0);
				}
				else {
					if(Integer.parseInt(newValue)>totalIndex) {
						System.out.println("hit2"+val+"\t"+newValue);
						setFrameByIndex(totalIndex-1);
					}
					else{
						System.out.println("hit3"+val+"\t"+newValue);
						currIndex = Integer.parseInt(val)-1; 
						setFrameByIndex(currIndex);
						}
				}
			}
		});
	}

	private void setButtonListeners() {
		btnInc1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	txtVessel1x.setText(""+(Integer.parseInt(txtVessel1x.getText())+1));
            }
        });
		btnDec1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel1x.setText(""+(Integer.parseInt(txtVessel1x.getText())-1));
			}
		});
		btnInc2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel1y.setText(""+(Integer.parseInt(txtVessel1y.getText())+1));
			}
		});
		btnDec2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel1y.setText(""+(Integer.parseInt(txtVessel1y.getText())-1));
			}
		});
		btnInc3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel2x.setText(""+(Integer.parseInt(txtVessel2x.getText())+1));
			}
		});
		btnDec3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel2x.setText(""+(Integer.parseInt(txtVessel2x.getText())-1));
			}
		});
		btnInc4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel2y.setText(""+(Integer.parseInt(txtVessel2y.getText())+1));
			}
		});
		btnDec4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVessel2y.setText(""+(Integer.parseInt(txtVessel2y.getText())-1));
			}
		});
		btnInc5.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtScaley1.setText(""+(Integer.parseInt(txtScaley1.getText())+1));
			}
		});
		btnDec5.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtScaley1.setText(""+(Integer.parseInt(txtScaley1.getText())-1));
			}
		});
		btnInc6.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtScaley2.setText(""+(Integer.parseInt(txtScaley2.getText())+1));
			}
		});
		btnDec6.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtScaley2.setText(""+(Integer.parseInt(txtScaley2.getText())-1));
			}
		});
		btnInc7.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVesselxScale.setText(""+(Integer.parseInt(txtVesselxScale.getText())+1));
			}
		});
		btnDec7.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtVesselxScale.setText(""+(Integer.parseInt(txtVesselxScale.getText())-1));
			}
		});
		btnInc10.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopLeftLimit.setText(""+(Integer.parseInt(txtDopLeftLimit.getText())+1));
			}
		});
		btnDec10.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopLeftLimit.setText(""+(Integer.parseInt(txtDopLeftLimit.getText())-1));
			}
		});
		btnInc11.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopRightLimit.setText(""+(Integer.parseInt(txtDopRightLimit.getText())+1));
			}
		});
		btnDec11.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopRightLimit.setText(""+(Integer.parseInt(txtDopRightLimit.getText())-1));
			}
		});
		btnInc12.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopUpperLimit.setText(""+(Integer.parseInt(txtDopUpperLimit.getText())+1));
			}
		});
		btnDec12.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopUpperLimit.setText(""+(Integer.parseInt(txtDopUpperLimit.getText())-1));
			}
		});
		btnInc13.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopLowerLimit.setText(""+(Integer.parseInt(txtDopLowerLimit.getText())+1));
			}
		});
		btnDec13.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopLowerLimit.setText(""+(Integer.parseInt(txtDopLowerLimit.getText())-1));
			}
		});
		btnInc14.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopZeroAxis.setText(""+(Integer.parseInt(txtDopZeroAxis.getText())+1));
			}
		});
		btnDec14.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				txtDopZeroAxis.setText(""+(Integer.parseInt(txtDopZeroAxis.getText())-1));
			}
		});
	}
	
	private void reDrawSetUpLines(int index) {
		drawInnerVesselLine();
		drawInnerVesselScale();
		drawDopplerROI();
		setFrameByIndex(index);
	}

	private Point dopplerpt1;
	private Point dopplerpt2;
	private double dopHScaleVal = 0;
	private double dopVScaleVal = 0;
	private void setDopplerPoints(double x, double y) {
		if(dopplerpt1==null) {
			System.out.println("setting 1");
			dopplerpt1 = scalePoint(x, y);
			System.out.println(dopplerpt1.toString());
		}
		else if(dopplerpt2==null) {
			setMode(-1);
			System.out.println("setting 2");
			dopplerpt2 = scalePoint(x, y);
			System.out.println(dopplerpt2.toString());
			setUpLinesMap.put(DPTS_KEY, new DrawParams(dopplerpt1, dopplerpt2, new Scalar(255,255,0), 2));
			setUpLinesMap.put(DCP1_KEY, new DrawParams(dopplerpt1, 10, new Scalar(255, 255, 255), 2));
			setUpLinesMap.put(DCP2_KEY, new DrawParams(dopplerpt2, 10, new Scalar(255, 255, 255), 2));
//			drawDopplerROI();
//			setFrameByIndex(currIndex);

			int dx1 = (int) (dopplerpt1.x<dopplerpt2.x?dopplerpt1.x:dopplerpt2.x);
			int dx2 = (int) (dopplerpt1.x>dopplerpt2.x?dopplerpt1.x:dopplerpt2.x);
			int dy1 = (int) (dopplerpt1.y<dopplerpt2.y?dopplerpt1.y:dopplerpt2.y);
			int dy2 = (int) (dopplerpt1.y>dopplerpt2.y?dopplerpt1.y:dopplerpt2.y);
			
			txtDopLeftLimit.setText(Integer.toString(dx1));
			txtDopUpperLimit.setText(Integer.toString(dy1));
			txtDopRightLimit.setText(Integer.toString(dx2));
			txtDopLowerLimit.setText(Integer.toString(dy2));
		}
		else {
//			dopplerpt2 = null;
//			dopplerpt2 = null;
//			matArray.set(currIndex, srcMatArray.get(currIndex).clone());
//			setFrameByIndex(currIndex);
		}
	}

	private void drawDopplerROI() {
		if(dopplerpt1!=null&&dopplerpt2!=null) {
			Imgproc.rectangle(matArray.get(currIndex), dopplerpt1, dopplerpt2, new Scalar(255,255,0), 2);
			Imgproc.circle(matArray.get(currIndex), dopplerpt1, 10, new Scalar(255, 255, 255), 2);
			Imgproc.circle(matArray.get(currIndex), dopplerpt2, 10, new Scalar(255, 255, 255), 2);
			setFrameByIndex(currIndex);
		}
		else {
			System.out.println("oops");
			System.out.println(dopplerpt1);
			System.out.println(dopplerpt2);
		}
	}

	private Point vesselScalept1;
	private Point vesselScalept2;
	private double pxlToCm = 0;
	private double vesselCmScale = 0;
	private boolean vsInit = false;
	private void setVesselPixelScale(double x, double y) {
		if(vesselScalept1==null) {
			System.out.println("setting 1");
			vesselScalept1 = scalePoint(x, y);
			System.out.println(vesselScalept1.toString());
		}
		else if(vesselScalept2==null) {
			setMode(-1);
			System.out.println("setting 2");
			vesselScalept2 = scalePoint(x, y);
			System.out.println(vesselScalept2.toString());
			int adjXval = (int) ((vesselScalept1.x+vesselScalept2.x)/2);
			vesselScalept1.x = adjXval;
			vesselScalept2.x = adjXval;
			setUpLinesMap.put(VSPTS_KEY, new DrawParams(vesselScalept1, vesselScalept2, new Scalar(0,255,0), 2));
			setUpLinesMap.put(VSP1_KEY, new DrawParams(new Point(vesselScalept1.x-10, vesselScalept1.y), new Point(vesselScalept1.x+10, vesselScalept1.y), new Scalar(0, 255, 0), 2));
			setUpLinesMap.put(VSP2_KEY, new DrawParams(new Point(vesselScalept2.x-10, vesselScalept2.y), new Point(vesselScalept2.x+10, vesselScalept2.y), new Scalar(0, 255, 0), 2));
//			drawInnerVesselScale();
			vsInit = true;
			setFrameByIndex(currIndex);
			txtScaley1.setText(Integer.toString((int) vesselScalept1.y));
			txtScaley2.setText(Integer.toString((int) vesselScalept2.y));
			txtVesselxScale.setText(Integer.toString(adjXval));
			vsInit = false;
		}
		else {
//			vesselScalept1 = null;
//			vesselScalept2 = null;
//			matArray.set(currIndex, srcMatArray.get(currIndex).clone());
//			setFrameByIndex(currIndex);
		}		
	}

	private void drawInnerVesselScale() {
		if(vesselScalept1!=null&&vesselScalept2!=null) {
			int adjXval = (int) ((vesselScalept1.x+vesselScalept2.x)/2);
			vesselScalept1.x = adjXval;
			vesselScalept2.x = adjXval;
			Imgproc.line(matArray.get(currIndex), vesselScalept1, vesselScalept2, new Scalar(0,255,0), 2);
			Imgproc.line(matArray.get(currIndex), new Point(vesselScalept1.x-10, vesselScalept1.y), new Point(vesselScalept1.x+10, vesselScalept1.y), new Scalar(0, 255, 0), 2);
			Imgproc.line(matArray.get(currIndex), new Point(vesselScalept2.x-10, vesselScalept2.y), new Point(vesselScalept2.x+10, vesselScalept2.y), new Scalar(0, 255, 0), 2);
			setFrameByIndex(currIndex);
			setVesselPixelScale();
		}
		else {
			System.out.println("oops");
			if(vesselScalept1==null)System.out.println("1 null");
			if(vesselScalept2==null)System.out.println("2 null");
		}
	}

	private void setVesselPixelScale() {
		if(vesselScalept1!=null&&vesselScalept2!=null&&vesselCmScale>0) {
			pxlToCm = vesselCmScale/Math.abs(vesselScalept1.y-vesselScalept2.y);
		}
	}

	private Point vesselpt1;
	private Point vesselpt2;
	private void setVesselPoints(double x, double y) {
		if(vesselpt1==null) {
			System.out.println("setting 1");
			vesselpt1 = scalePoint(x, y);
			System.out.println(vesselpt1.toString());
		}
		else if(vesselpt2==null) {
			setMode(-1);
			System.out.println("setting 2");
			vesselpt2 = scalePoint(x, y);
			System.out.println(vesselpt1.toString());
			setUpLinesMap.put(VPTS_KEY, new DrawParams(vesselpt1, vesselpt2, new Scalar(0,0,255), 2));
			setUpLinesMap.put(VCP1_KEY, new DrawParams(vesselpt1, 10, new Scalar(255, 255, 255), 2));
			setUpLinesMap.put(VCP2_KEY, new DrawParams(vesselpt2, 10, new Scalar(255, 255, 255), 2));
//			drawInnerVesselLine();
//			setFrameByIndex(currIndex);
			txtVessel1x.setText(Integer.toString((int) vesselpt1.x));
			txtVessel1y.setText(Integer.toString((int) vesselpt1.y));
			txtVessel2x.setText(Integer.toString((int) vesselpt2.x));
			txtVessel2y.setText(Integer.toString((int) vesselpt2.y));
		}
		else {
//			vesselpt1 = null;
//			vesselpt2 = null;
//			matArray.set(currIndex, srcMatArray.get(currIndex).clone());
//			setFrameByIndex(currIndex);
		}
	}

	private void drawInnerVesselLine() {
		if(vesselpt1!=null&&vesselpt2!=null) {
			Imgproc.line(matArray.get(currIndex), vesselpt1, vesselpt2, new Scalar(0,0,255), 2);
			Imgproc.circle(matArray.get(currIndex), vesselpt1, 10, new Scalar(255, 255, 255), 2);
			Imgproc.circle(matArray.get(currIndex), vesselpt2, 10, new Scalar(255, 255, 255), 2);
			setFrameByIndex(currIndex);
		}
		else {
			System.out.println("oops");
			if(vesselpt1==null)System.out.println("1 null");
			if(vesselpt2==null)System.out.println("2 null");
		}
	}

	private Point scalePoint(double x, double y) {
		double xScale = frameSize.width/currentFrame.getFitWidth(); 
		double yScale = frameSize.height/currentFrame.getFitHeight(); 
		return new Point(xScale*x, yScale*y);
	}

	private void setFrameByIndex(int index) {
		drawLinesForFrame(index);
		Image img = Utils.mat2Image(matArray.get(index));
		currentFrame.setImage(img);
		txtfCurrFrame.setText(Integer.toString(currIndex+1));
		//		if(index<diaArray.size())System.out.println(diaArray.get(index));
		if(index%100==0)System.out.println("Progress\t"+index);
	}
}
