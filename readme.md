# CT_Images

[![dependencies](https://img.shields.io/badge/Dependencies-Processing_2.x-blue)](https://processing.org/)
![language](https://img.shields.io/badge/Language-Java-red)

## 1. Introduction

### 1.1 CT window

The original CT image data have pixels with gray scale value ranging from 0-2000. However, the image file only support
256 levels of gray scale. Thus, an interval should be selected to parse the CT image data, such as [500,755]. The pixels
having value less than 500 will be parsed to 0, those having value greater than 755 will be parsed to 255.

### 1.2 CT volume and mutate CT images

CT-volume is a set of continuous images collected. In a single CT volume, the images should have same CT window.
Manually collected CT volume may have different CT window, which is caused by human error. The most general case is
small clusters of images having mutate CT window in a CT volume, which can be identified by human eyes. (Those images
here refer as "mutate CT images")

## 2. The Program

### 2.1 Core methodology

The differences of the CT window reflects on the average and standard deviation of the images (
average/standard-deviation of image pixels' grayscale value) also the gray level histogram. The images having mutate CT
window have significant difference in standard deviation and average of pixels. By constructing a confidence interval
using the pixels' average and standard deviation, we are able to find the out-range images. However, the image found
using above method may not be the images having mutate CT window. Another significant feature is whether of value (data
point) in the border of the mutate cluster is relatively continuous with other images.

Steps taken to judge whether the selected cluster is relatively continuous with other images:
1. calculate the standard deviation inside the cluster.

2. construct a confidence interval using the standard deviation and the border value. That is [border value +- sd]

3. Test whether the adjacent value out of the mutate cluster with in the newly constructed confidential interval. If
   not, the cluster is relatively continuous with other images, that is the cluster is not mutate cluster. Otherwise,
   the cluster is considered as a mutate cluster, and marked.

### 2.2 Build, run and use the program.

#### 2.2.1 Build and run

This program is using [Processing](https://processing.org/) as its library to load and display the images. If you are
building this project yourself, make sure the library is in 2.x version. Otherwise, the images won't be successfully
loaded.
(As Processing 3 do not allow users to load images out of setup(),
see [Processing documentation](https://processing.org/reference/loadImage_.html) for loadImage() method). There is
basically no limitation for Java version (jdk 1.8 or more), however, for jdk 1.8 on Linux, this program may not work
properly due to other dependencies (The system-level dependencies used by the library).
#### 2.2.2 Usage
This program has a command line interface. Syntax of commands as follows: 
##### load
```
load [path] [header] [start index] [format]
```
For example,
```
load ~/Pictures/CT_images/HEP0001 Se2Im 30 jpg
```
The images within a CT volume have file name with same header and continuous indices, so the header and start index is used to 
generate a series of filename and locate the CT-volume.
##### 






