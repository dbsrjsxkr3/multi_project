#include "module.h"
#include "log.h"

extern "C" {

int process(Mat img_input, Mat &img_result) {
    cvtColor(img_input, img_result, CV_RGBA2GRAY);

    return (0);
}
}

