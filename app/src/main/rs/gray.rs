#pragma version(1)
#pragma rs java_package_name(com.example.mybitmap.imageprocessing.rsclass)

uchar4 RS_KERNEL gray(uchar4 in) {

    float4 pixelf = rsUnpackColor8888(in);

    float gray = (0.30 * pixelf.r + 0.59 * pixelf.g + 0.11 * pixelf.b);

    return rsPackColorTo8888(gray , gray , gray , pixelf.a);
}
