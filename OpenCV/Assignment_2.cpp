/**
 * Created by stefan on 07-10-22.
 * This program detects distance between to holes in a bracket.
 * This is done with the help of Canny edge detection and Hough Circles.
 * After detecting all edges we can look for circles using Hough Circles.
 * All result from the `HoughCircles()` function are stored in a Vector list.
 * When the list is filled we loop trough the list and draw a circle on the X / Y location.
 * If their are 2 circles found in a image, we draw a line between them and we calculate the distance between the 2 X/Y locations.
 * The found circles are marked in the source image with a green dot (center point) and a yellow circle (same size as found circle).
 * Lastly we add the distance between the two circles.
*/

#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>
#include <filesystem>

namespace fs = std::filesystem;
using namespace std;
using namespace cv;

#define SAMPLES "/home/stefan/Work/Motion_vision/OpenCV/Assignment_2"

#define thres1 85
#define thres2 (thres1 * 3)

Mat src, gray;
Mat detected_edges;

int main() {
    cout << "OpenCV, Bracket" << endl;

    for (const auto &entry: fs::directory_iterator(SAMPLES)) {
        try {
            // Load src image
            src = imread(entry.path(), IMREAD_COLOR);
            cvtColor(src, gray, COLOR_BGR2GRAY);
            blur(gray, detected_edges, Size(3, 3));

            // Edge detection
            Canny(detected_edges, detected_edges, thres1, thres2, 3);
            imshow("detected_edges", detected_edges);

            vector<Vec3f> circles;
            HoughCircles(detected_edges, circles, HOUGH_GRADIENT, 1,
                         static_cast<float>(detected_edges.rows) / 16.0F,
                         100, 30, 1, 80);

            for (size_t i = 0; i < circles.size(); i++) {
                Point center = Point2f(circles[i][0], circles[i][1]);
                // circle center
                circle(src, center, 1, Scalar(0, 255, 0), 3, LINE_AA);
                // circle outline
                int radius = static_cast<int>(circles[i][2]);
                circle(src, center, radius, Scalar(0, 255, 255), 3, LINE_AA);

                // Check if we have 2 holes
                if (circles.size() == 2) {

                    Point left_circle = Point2f(circles[0][0], circles[0][1]);
                    Point right_circle = Point2f(circles[1][0], circles[1][1]);

                    // Draw line between the two holes
                    line(src,
                         left_circle,
                         right_circle,
                         Scalar(255, 0, 0), 1, LINE_AA);

                    // Calculate distance between the left and right hole
                    double dis = norm(left_circle - right_circle);
                    // Make the text for in the image
                    char buffer[100];
                    snprintf(buffer, 100, "Distance between points: %2.1f", dis);
                    putText(src, buffer, Point(10, 25), FONT_HERSHEY_SIMPLEX, 0.75, Scalar(0, 0, 0), 2);
                }

            }

            imshow(entry.path().filename(), src);
            waitKey();

        } catch (const std::exception &e) {
            cout << e.what() << endl;
        }
    }
    destroyAllWindows();
    return 0;
}
