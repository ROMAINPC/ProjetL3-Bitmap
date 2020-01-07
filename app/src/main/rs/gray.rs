#pragma version(1)
#pragma rs java_package_name(fr.romainpc.bitmapproject.imageprocessing.rsclass)

float redWeight;
float greenWeight;
float blueWeight;

uchar4 RS_KERNEL gray(uchar4 in) {

    float4 pixelf = rsUnpackColor8888(in);

    float gray = (redWeight * pixelf.r + greenWeight * pixelf.g + blueWeight * pixelf.b);

    return rsPackColorTo8888(gray , gray , gray , pixelf.a);
}
