#pragma version(1)
#pragma rs java_package_name(fr.romainpc.bitmapproject.imageprocessing.rsclass)

float hueAngle;
float toleranceAngle;

uchar4 RS_KERNEL keep_color(uchar4 in) {

    hueAngle = fmod(hueAngle, 360);
    toleranceAngle = fmod(toleranceAngle, 180);

    //Convert pixel from RGB to HSV :
    float4 pixelf = rsUnpackColor8888(in);
    float maxi = max(max(pixelf.r, pixelf.g), pixelf.b);
    float mini = min(min(pixelf.r, pixelf.g), pixelf.b);
    float h,s,v;

    if (maxi == mini) {
            h = 0;
    } else if (maxi == pixelf.r) {
        h = (60 *((pixelf.g - pixelf.b) / (maxi - mini)) + 360);
        while (h > 360){ h -= 360;}
    } else if (maxi == pixelf.g) {
                    h = (60 * ((pixelf.b - pixelf.r) / (maxi - mini)) + 120);
    } else if (maxi == pixelf.b) {
                    h = (60 * ((pixelf.r - pixelf.g) / (maxi - mini)) + 240);
    }

    if (maxi == 0) {
        s = 0;
    } else {
        s = (float)1 - (mini / maxi);
    }

    v = maxi;


    //apply effect:
    float diff = fabs(h - hueAngle);
    if (!(min(diff, 360 - diff) <= toleranceAngle)) {
         s = 0;
    }


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