# CT_Images
![dependencies](https://img.shields.io/badge/Dependencies-Processing_2.x-blue)
![language](https://img.shields.io/badge/Language-Java-red)


## 1. Introduction
### 1.1 CT window
The original CT image data have pixels with gray scale value ranging from 0-2000.
However, the image file only support 256 levels of gray scale. Thus, an interval should be selected to parse the CT image data, such as [500,755]. The pixels having value less than 500 will be parsed to 0, those having value greater than 755 will be parsed to 255.

### 1.2 CT volume and mutate CT images
CT-volume is a set of continuous images collected. 
In a single CT volume, the images should have same CT window. 
Manually collected CT volume may have different CT window, which is caused by human error. The most general case is small clusters of images having mutate CT window in a CT volume, which can be identified by human eyes. (Those images here refer as "mutate CT images")

## 2. The Program designed 
### 2.1 The methodology
The differences of the CT window reflects on the average and standard deviation of the images (average/standard-deviation of 
image pixels' grayscale value) also the gray level histogram. The images having mutate CT window have significant difference 
in standard deviation and average of pixels. By constructing a confidence interval using the pixels' average and standard deviation,
we are able to find the out-range images. However, the image found using above method may not be the images having mutate CT window.





