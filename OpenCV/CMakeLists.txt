cmake_minimum_required(VERSION 3.23)
project(OpenCV)

set(CMAKE_CXX_STANDARD 20)

set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/opencv-build")

find_package( OpenCV REQUIRED )

IF(${OpenCV_FOUND})
    message(STATUS "Found OpenCV")
    message(STATUS "    version: ${OpenCV_VERSION}")
    include_directories( ${OpenCV_INCLUDE_DIRS} )
ENDIF()


file( GLOB APP_SOURCES ${CMAKE_CURRENT_SOURCE_DIR}/*.cpp )

foreach( sourcefile ${APP_SOURCES} )
    file(RELATIVE_PATH filename ${CMAKE_CURRENT_SOURCE_DIR} ${sourcefile})
    string( REPLACE ".cpp" "" name_exe ${filename} )
    add_executable( ${name_exe} ${sourcefile} )
    target_link_libraries( ${name_exe} ${OpenCV_LIBS})
endforeach( sourcefile ${APP_SOURCES} )