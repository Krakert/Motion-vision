/**
 * Created by stefan on 12-10-22.
 * This program detects the amount of dots on a dice
 * This is done with the help of `SimpleBlobDetector`
 * All found blobs add added to a Vector list, after that we loop over the list and place a green dot in the center of
 * the blob. Lastly we add the amount of found dots (size Vector list of blobs) to the source image.
*/

#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>
#include <filesystem>

namespace fs = std::filesystem;
using namespace std;
using namespace cv;

#define SAMPLES "/home/stefan/Work/Motion_vision/OpenCV/Assignment_9"

Mat src;

int main() {
    cout << "OpenCV, Blob" << endl;

    for (const auto &entry: fs::directory_iterator(SAMPLES)) {
        try {
            // Load src image
            src = imread(entry.path(), IMREAD_COLOR);

            Ptr<cv::SimpleBlobDetector> detector = cv::SimpleBlobDetector::create();
            //Detect blobs
            vector<cv::KeyPoint> blobs;
            detector->detect(src, blobs);

            for (auto & blob : blobs) {
                Point center = Point2f(blob.pt.x, blob.pt.y);
                // Blob center
                circle(src, center, 1, Scalar(0, 255, 0), 3, LINE_AA);
            }

            char buffer[100];
            snprintf(buffer, 100, "Found dots: %zu", blobs.size());
            putText(src, buffer, Point(10, 25), FONT_HERSHEY_SIMPLEX, 0.75, Scalar(0, 0, 0), 2);
            imshow("How many dots?!", src);
            waitKey();

        } catch (const std::exception &e) {
            cout << e.what() << endl;
        }
    }
    destroyAllWindows();
    return 0;
}
