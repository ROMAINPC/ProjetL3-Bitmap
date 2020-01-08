#pragma version(1)
#pragma rs java_package_name(fr.romainpc.bitmapproject.imageprocessing.rsclass)

float hueAngle;

uchar4 RS_KERNEL hue(uchar4 in) {

    //Convert pixel from RGB to HSV :
    float4 pixelf = rsUnpackColor8888(in);
    float maxi = max(max(pixelf.r, pixelf.g), pixelf.b);
    float mini = min(min(pixelf.r, pixelf.g), pixelf.b);
    float h,s,v;

    //don't need h

    if (maxi == 0) {
        s = 0;
    } else {
        s = (float)1 - (mini / maxi);
    }

    v = maxi;


    //apply effect:
    h = fmod(hueAngle , 360);


    //convert HSV to RGB:
    int t = (int) fmod((h / 60) , 6);
    float C = s * v;
    float X = C * (1 - fabs(fmod((h / 60) , 2) - 1));
    float m = v - C;
    float r = 0; float g = 0; float b = 0;
    switch (t) {
                case 0:
                    r = C;
                    g = X;
                    b = 0;
                    break;
                case 1:
                    r = X;
                    g = C;
                    b = 0;
                    break;
                case 2:
                    r = 0;
                    g = C;
                    b = X;
                    break;
                case 3:
                    r = 0;
                    g = X;
                    b = C;
                    break;
                case 4:
                    r = X;
                    g = 0;
                    b = C;
                    break;
                case 5:
                    r = C;
                    g = 0;
                    b = X;
                    break;
    }

    return rsPackColorTo8888(r + m , g + m , b + m , pixelf.a);
}