/**
 * Created by stefan on 07-10-22.
 * This program detects the amount of wires that are present in a connector
 * This is done with the help of Canny edge detection
 * Edges will be marked in white, so we can loop through the Y axis on a Given X position, when the value of the pixel
 * is white (255) we have found a edge, a wire has 2 edges, so 4 wires will have 8 edges (4x2).
 * The found edges are marked in the source image with a green dot, also we add the amount of wires found (edges / 2)
*/

#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>
#include <filesystem>

namespace fs = std::filesystem;
using namespace std;
using namespace cv;

#define SAMPLES "/home/stefan/Work/Motion_vision/OpenCV/Assignment_1"

Mat src, canny;

int main() {
    cout << "OpenCV, Connectors" << endl;

    for (const auto &entry: fs::directory_iterator(SAMPLES)) {
        try {
            // Load src image
            src = imread(entry.path(), IMREAD_COLOR);
            // Edge detection
            Canny(src, canny, 70, 200, 3);

            imshow("canny", canny);

            int x, y, nrIntersections = 0;
            x = canny.cols - 75;

            for (y = 0; y < canny.rows; y++) {
                // If intersection found (white pixel)
                if ((int) canny.at<uchar>(y, x) == 255) {
                    nrIntersections++;
                    Point center = Point(x, y);
                    circle(src, center, 1, Scalar(0, 255, 0), 3, LINE_AA);
                }
            }

            // Make the text for in the image
            char buffer[100];
            snprintf(buffer, 100,  "Found: %d wires", nrIntersections / 2);
            putText(src, buffer, Point2f(10, 25), FONT_HERSHEY_SIMPLEX, 0.75, Scalar(0, 0, 0), 2);

            imshow(entry.path().filename(), src);
            waitKey(0);

        } catch (const std::exception& e) {
            cout << e.what() << endl;
        }
    }
    destroyAllWindows();
    return 0;
}
