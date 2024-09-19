//package com.example.tars01.Printer;
//
//import javafx.scene.image.WritableImage;
//import zj.com.customize.sdk.Other;
//import android.graphics.Bitmap;
//
//public class PrintPicture {
//
//
//    public static byte[] POS_PrintBMP(WritableImage mBitmap, int nWidth, int nMode) {
//		// 先转黑白，再调用函数缩放位图
//		int width = ((nWidth + 7) / 8) * 8;
//		int height = (int) (mBitmap.getHeight() * width / mBitmap.getWidth());
//		height = ((height + 7) / 8) * 8;
//
//		WritableImage rszBitmap = mBitmap;
//		if (mBitmap.getWidth() != width){
//			rszBitmap = Other.resizeImage(mBitmap, width, height);
//		}
//
//		WritableImage grayBitmap = Other.toGrayscale(rszBitmap);
//
//		byte[] dithered = Other.thresholdToBWPic(grayBitmap);
//
//		byte[] data = Other.eachLinePixToCmd(dithered, width, nMode);
//
//		return data;
//	}
//
//    /**
//     * 使用下传位图打印图片
//	 * 先收完再打印
//     * @param bmp
//     * @return
//     */
//	public static byte[] Print_1D2A(Bitmap bmp){
//
//			/*
//			 * 使用下传位图打印图片
//			 * 先收完再打印
//			 */
//	 		int width = bmp.getWidth();
//	 		int height = bmp.getHeight();
//	 		byte data[]=new byte[1024*10];
//			data[0] = 0x1D;
//			data[1] = 0x2A;
//			data[2] =(byte)( (width - 1)/ 8 + 1);
//			data[3] =(byte)( (height - 1)/ 8 + 1);
//			byte k = 0;
//			int position = 4;
//			int i;
//			int j;
//			byte temp = 0;
//			for(i = 0; i <width;  i++){
//
//				System.out.println("进来了...I");
//				for(j = 0; j < height; j++){
//					System.out.println("进来了...J");
//					if(bmp.getPixel(i, j) != -1){
//						temp |= (0x80 >> k);
//					} // end if
//					k++;
//					if(k == 8){
//						data[position++] = temp;
//						temp = 0;
//						k = 0;
//					} // end if k
//				}// end for j
//				if(k % 8 != 0){
//					data[position ++] = temp;
//					temp = 0;
//					k = 0;
//				}
//
//			}
//			System.out.println("data"+data);
//
//			if( width% 8 != 0){
//				i =   height/ 8;
//				if(height % 8 != 0) i++;
//				j = 8 - (width % 8);
//				for(k = 0; k < i*j; k++){
//					data[position++] = 0;
//				}
//			}
//			return data;
//		}
//
//
//	public static WritableImage resizeImage(WritableImage bitmap, int w, int h) {
//		Bitmap BitmapOrg = bitmap;
//		int width = BitmapOrg.getWidth();
//		int height = BitmapOrg.getHeight();
//		int newWidth = w;
//		int newHeight = h;
//		float scaleWidth = (float)newWidth / (float)width;
//		float scaleHeight = (float)newHeight / (float)height;
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
//		return resizedBitmap;
//	}
//
//	public static byte[] thresholdToBWPic(WritableImage mBitmap) {
//		int[] pixels = new int[(int) (mBitmap.getWidth() * mBitmap.getHeight())];
//		byte[] data = new byte[(int) (mBitmap.getWidth() * mBitmap.getHeight())];
//		mBitmap.getPixelReader()(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
//		format_K_threshold(pixels, mBitmap.getWidth(), mBitmap.getHeight(), data);
//		return data;
//	}
//
//}
