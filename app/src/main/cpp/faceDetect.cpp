//
// Created by 강지연 on 4/12/21.
//
#define DLIB_JPEG_SUPPORT

#include <jni.h>
#include <dlib/image_processing/frontal_face_detector.h>
#include <dlib/image_processing/render_face_detections.h>
#include <dlib/image_processing.h>
#include <dlib/image_transforms.h>
#include <dlib/image_io.h>
#include <iostream>
#include <android/log.h>

using namespace dlib;
using namespace std;

extern "C" JNIEXPORT void JNICALL
Java_com_example_testapplication_MainActivity_Detect(JNIEnv *env, jobject thiz) {

    __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                        "start");

    try {
        // We need a face detector.  We will use this to get bounding boxes for
        // each face in an image.
        frontal_face_detector detector = get_frontal_face_detector();

        // And we also need a shape_predictor.  This is the tool that will predict face
        // landmark positions given an image and face bounding box.  Here we are just
        // loading the model from the shape_predictor_68_face_landmarks.dat file you gave
        // as a command line argument.
        shape_predictor sp;
        deserialize("/storage/emulated/0/shape_predictor_68_face_landmarks.dat") >> sp;

        //image_window win, win_faces;
        // Loop over all the images provided on the command line.
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "load shape_predictor_68_face_landmarks");


        array2d<rgb_pixel> img;
        load_image(img, "/storage/emulated/0/camtest/1.bmp");
        // Make the image larger so we can detect small faces.
        // pyramid_up(img);

        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "load image %d x %d");

        array2d<rgb_pixel> sizeImg(1280, 960);
        resize_image(img, sizeImg);
        ;

        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "start detect");

        // Now tell the face detector to give us a list of bounding boxes
        // around all the faces in the image.
        std::vector<rectangle> dets = detector(sizeImg, 0);
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "Number of faces detected:  %d", (int) dets.size());

        for ( auto & rect : dets)
            dlib :: draw_rectangle (sizeImg, rect, dlib :: rgb_pixel ( 255 , 0 , 0 ), 3 );



        // Now we will go ask the shape_predictor to tell us the pose of
        // each face we detected.
        std::vector<full_object_detection> shapes;
        for (unsigned long j = 0; j < dets.size(); ++j)
        {
            full_object_detection shape = sp(img, dets[j]);
            cout << "number of parts: "<< shape.num_parts() << endl;

            for( int i=0; i<shape.num_parts(); i++){

                point p = shape.part(i);

                dlib :: draw_solid_circle(sizeImg, p, 3, dlib :: rgb_pixel ( 255 , 0 , 0 ));
            }
        }

        dlib :: save_bmp (sizeImg, "/storage/emulated/0/camtest/output.bmp" );


    } catch (exception& e)
    {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "exception thrown! %s",  e.what() );
    }


}
