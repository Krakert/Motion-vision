/**
 * Created by stefan on 12-10-22.
 * This program detects if a pin is bent in a 2,54mm pitched GPIO header
 * This is done with the help of Canny edge detection, `findContours()` and `boundingRect()`
 * After detecting all edges we can look for contours based on the edges
 * All result from the `findContours()` function are stored in a Vector list.
 * Based on those result we can calculate the up-right bounding rectangle of a point set (item from the vector list)
 * When a pin is bent the width of the up-right bounding rectangle will be greater (| vs \), taking up more in the X axis.
 * We check if the up-right bounding rectangle width is greater then a given threshold, when greater we define the pin as bent,
 * the center of the up-right bounding rectangle is used to add a circle to the source image, indicating that the pin is bent.
 * Lastly we add the amount of bent pins to the source image.
*/

#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>
#include <filesystem>

namespace fs = std::filesystem;
using namespace std;
using namespace cv;

#define SAMPLES "/home/stefan/Work/Motion_vision/OpenCV/Assignment_7"

#define threshold_bend 15
#define thres1 85
#define thres2 (thres1 * 3)

Mat src, blurred;
Mat detected_edges;


int main() {
    cout << "OpenCV, Pins" << endl;

    for (const auto &entry: fs::directory_iterator(SAMPLES)) {
        try {
            // Load src image
            src = imread(entry.path(), IMREAD_COLOR);
            // Blur
            medianBlur(src, blurred, 3);
            // Set threshold
            threshold(blurred, blurred, 100, 255, cv::THRESH_BINARY);

            // Edge detection
            Canny(blurred, detected_edges, thres1, thres2, 3);

            vector<vector<Point> > contours;
            // Find all the contours in the image
            findContours(detected_edges, contours, RETR_TREE, CHAIN_APPROX_SIMPLE);

            vector<Rect> boundRect(contours.size());

            int bendPins = 0;

            for (size_t i = 0; i < contours.size(); i++) {

                // Calculate the size of the straight Bounding Rectangle.
                // The greater the width, the more bent the pin is.
                boundRect[i] = boundingRect(contours[i]);

                Rect rect(boundRect[i].tl(), boundRect[i].br());

                // When a pin it to bend
                if (rect.width > threshold_bend) {
                    Point centerRect = (boundRect[i].br() + boundRect[i].tl()) * 0.5;
                    cout << "X: " << centerRect.x << " Y: " << centerRect.y << endl;
                    circle(src, centerRect, 5, Scalar(0 , 0,255), 2);
                    bendPins++;
                }
            }

            char buffer[100];
            snprintf(buffer, 100, "Found bent pin(s) : %d", bendPins);
            putText(src, buffer, Point(10, 25), FONT_HERSHEY_SIMPLEX, 0.75, Scalar(255, 255, 255), 2);

            imshow(entry.path().filename(), src);

            waitKey();

        } catch (const std::exception &e) {
            cout << e.what() << endl;
        }
    }
    destroyAllWindows();
    return 0;
}
