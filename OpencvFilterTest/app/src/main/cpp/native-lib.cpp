#include <jni.h>
#include "module.h"
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <jni.h>

#include <opencv2/opencv.hpp>
//#include <android/asset_manager_jni.h>
#include <android/log.h>
//#include <string>


using namespace cv;
using namespace std;


extern "C" {

JNIEXPORT jint JNICALL
Java_com_example_yoon_myapplication_MainActivity_convertNativeLib(JNIEnv*, jobject, jlong addrInput, jlong addrResult) {

    Mat &img_input = *(Mat *) addrInput;
    Mat &img_result = *(Mat *) addrResult;

    cvtColor(img_input, img_result, CV_RGBA2GRAY);
    return 1;
}

JNIEXPORT jint JNICALL
Java_com_example_yoon_myapplication_MainActivity_ThresholdLib(JNIEnv*, jobject, jlong addrInput, jlong addrResult) {
    Mat &img_input = *(Mat *) addrInput;
    Mat &img_result = *(Mat *) addrResult;
    cvtColor(img_input,img_result,CV_RGB2GRAY);
    Mat srcImage = img_result;

    Mat destImage1;
    adaptiveThreshold(srcImage,destImage1,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,21,5);
    img_result = destImage1.clone();

    return(0);
}





JNIEXPORT jstring JNICALL
Java_com_example_yoon_myapplication_MainActivity_convertNativeLibtoBoxFilter2(JNIEnv *env, jobject, jlong addrInput, jlong addrResult) {

Mat &img_input = *(Mat *) addrInput;
Mat &img_result = *(Mat *) addrResult;
//cvtColor(img_input, img_result, CV_RGBA2GRAY);
jstring result;
std::stringstream buffer;
Mat srcImage = img_result;

int border = 3;
Size ksize (border*2+1,border*2+1);

Mat destImage2;
int d = ksize.width;
double sigmaColor =10.0;
double sigmaSpace = 10.0;
//bilateralFilter(srcImage,destImage2,-1,sigmaColor,sigmaSpace);
    boxFilter(srcImage,destImage2,-1,ksize);

img_result = destImage2.clone();
buffer << "Box Filter, d= -1, 7 x 7, bilateralFilter"<<std::endl;

const char *cstr = buffer.str().c_str();
result = env->NewStringUTF(cstr);

return result;
}





JNIEXPORT void JNICALL
Java_com_example_yoon_myapplication_MainActivity_addSaltAndPepperNoise(JNIEnv *env,
                                                                       jobject instance,
                                                                       jlong addrInput,
                                                                       jlong addrResult) {
    Mat &img_input = *(Mat *) addrInput;
    Mat &img_result = *(Mat *) addrResult;
    img_result=img_input;
    double noise_ratio=0.1;

    int rows = img_input.rows;
    int cols = img_input.cols;
    int ch = img_input.channels();
    int num_of_noise_pixels = (int)((double)(rows * cols * ch)*noise_ratio);

    for (int i = 0; i < num_of_noise_pixels; i++)
    {
        int r = rand() % rows;  // noise로 바꿀 행을 임의로 선택
        int c = rand() % cols;  // noise로 바꿀 열을 임의로 선택
        int _ch = rand() % ch;  // noise로 바꿀 채널의 임의로 선택

        // img.ptr<uchar>(r)은 r번째 행의 첫번째 픽셀, 첫번째 채널에 대한 주소값을 반환한다.
        uchar* pixel = img_result.ptr<uchar>(r) +(c*ch) + _ch; // noise로 바꿀 정확한 위치를 계산

        *pixel = (rand() % 2 == 1) ? 255 : 0; // black(0) 혹은 white(255)로 교체
    }
}


JNIEXPORT void JNICALL
Java_com_example_yoon_myapplication_MainActivity_gaussiannoise(JNIEnv *env, jobject instance,
                                                               jlong addrInput,
                                                               jlong addrResult) {

    Mat &img_input = *(Mat *) addrInput;
    Mat &img_result = *(Mat *) addrResult;
    img_result=img_input;

    resize(img_result, img_result, Size(), 0.3, 0.3, CV_INTER_AREA);


    Mat noise_image(img_result.size(), CV_16SC3);

    double average = 0.0;
    double std = 30.0;
    randn(noise_image, Scalar::all(average), Scalar::all(std));



    Mat temp_image;
    img_result.convertTo(temp_image, CV_16SC3);


     addWeighted(temp_image, 1.0, noise_image, 1.0, 0.0, temp_image);
    temp_image.convertTo(temp_image, img_result.type());

}





JNIEXPORT jlong JNICALL
Java_com_example_yoon_myapplication_CameraActivity_loadCascade(JNIEnv *env, jclass type,
                                                               jstring cascadeFileName_) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);


    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
}


float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}


JNIEXPORT void JNICALL
Java_com_example_yoon_myapplication_CameraActivity_detect(JNIEnv *env, jclass type,
                                                             jlong cascadeClassifier_face,
                                                             jlong cascadeClassifier_eye,
                                                             jlong matAddrInput,
                                                             jlong matAddrResult)
{
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );


    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());

    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);
        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(255, 0, 255), 30, 8, 0);


        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);
        Mat faceROI = img_gray( face_area );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(30, 30) );

        for ( size_t j = 0; j < eyes.size(); j++ )
        {
            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2, real_facesize_y + eyes[j].y + eyes[j].height/2 );
            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
            circle( img_result, eye_center, radius, Scalar( 255, 0, 0 ), 30, 8, 0 );
        }
    }
}


}
