package com.sark110.sarkplotsandroid;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This file is a part of the "SARK110 Antenna Vector Impedance Analyzer" software
 *
 * MIT License
 *
 * @author Copyright (c) 2018 Melchor Varela - EA4FRB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public abstract class DeviceIntf {
    static final int COMMAND_LEN = 18;

    Context mContext;

    boolean mConnected;

    private int mProtocolVer = 0;
    private byte[] mSarkVer = null;

    public int getProtocolVer() {
        return mProtocolVer;
    }

    public byte[] getSarkVer() {
        return mSarkVer;
    }

    DeviceIntf() {
    }
    DeviceIntf(Context context){
        this.mContext = context;
    }
    boolean isConnected() {
        return mConnected;
    }
    abstract void onCreate ();
    abstract void onResume ();
    abstract void connect();
    abstract int SendRcv(byte snd[], byte rcv[]);
    abstract void close();

    /* Listener handling */
    DeviceIntfListener mListener;

    public void setDeviceIntfListener(DeviceIntfListener listener) {
        this.mListener = listener;
    }

    public interface DeviceIntfListener {
        /**
         * Event fired when the connection status changes.
         * The event is also fired when the Connect() method ends, returning the result of the Connect() request.
         */
        void onConnectionStateChanged(DeviceIntf helper, boolean isConnected);
    }

    public int VersionCmd ()
    {
        int status;

        byte snd[] = new byte[COMMAND_LEN];
        byte rcv[] = new byte[COMMAND_LEN];
        snd[0] = 1;
        status = SendRcv(snd, rcv);
        if (rcv[0] != 'O')
            status = -1;
        if (status > 0) {
            mProtocolVer = Buf2Short(rcv, 1);
            mSarkVer = new byte[COMMAND_LEN];
            System.arraycopy(rcv, 3, mSarkVer, 0, COMMAND_LEN - 3);
        }
        return status;
    }

    public int BeepCmd()
    {
        int status;

        byte snd[] = new byte[COMMAND_LEN];
        byte rcv[] = new byte[COMMAND_LEN];
        snd[0] = 20;
        status = SendRcv(snd, rcv);
        if (rcv[0] != 'O')
            status = -1;
        return status;
    }

    public MeasureDataBin MeasureCmd(float freq)
    {
        int status;

        byte snd[] = new byte[COMMAND_LEN];
        byte rcv[] = new byte[COMMAND_LEN];

        snd[0] = 2;
        System.arraycopy( Int2Buf ((int)(freq*1000000)), 0, snd, 1, 4 );
        snd[5] = 1;
        snd[6] = 0;
        status = SendRcv(snd, rcv);
        if (rcv[0] != 'O')
            status = -1;
        if (status >= 0) {
            float Rs = Buf2Float(rcv, 1);
            float Xs = Buf2Float(rcv, 5);
            float S21R = Buf2Float(rcv, 9);
            float S21X = Buf2Float(rcv, 13);

            return new MeasureDataBin(0, freq, Rs, Xs);
        }
        else
            return null;
    }

    private ComplexNumber Z2Rho(ComplexNumber cxZ, ComplexNumber cxZ0)
    {
        return ComplexNumber.divide(ComplexNumber.subtract(cxZ, cxZ0), ComplexNumber.add(cxZ, cxZ0));
    }

    private float Z2Vswr (ComplexNumber cxZ, ComplexNumber cxZ0)
    {
        ComplexNumber cxRho = Z2Rho(cxZ, cxZ0);
        if (cxRho.mod() > 0.980197824)
            return 99.999f;
        return (1.0f + (float)cxRho.mod()) / (1.0f - (float)cxRho.mod());
    }

    private byte[] Int2Buf(int val)
    {
        byte[] buf = new byte[4];

        buf[3] = (byte)((val&0xff000000)>>24);
        buf[2] = (byte)((val&0x00ff0000)>>16);
        buf[1] = (byte)((val&0x0000ff00)>>8);
        buf[0] = (byte)((val&0x000000ff)>>0);

        return buf;
    }

    private int Buf2Short (byte buf[], int n)
    {
        int val;
        byte[] bufShort = new byte[2];

        System.arraycopy(buf, n, bufShort, 0, 2);
        val = bufShort[1] << 8;
        val += bufShort[0] << 0;

        return val;
    }

    private float Buf2Float (byte buf[], int n)
    {
        byte[] bufFloat = new byte[4];
        System.arraycopy(buf, n, bufFloat, 0, 4);
        return ByteBuffer.wrap(bufFloat).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    /*
     * "Half-float" conversion functions
     */
    // ignores the higher 16 bits
    private float toFloat( int hbits )
    {
        int mant = hbits & 0x03ff;            // 10 bits mantissa
        int exp =  hbits & 0x7c00;            // 5 bits exponent
        if( exp == 0x7c00 )                   // NaN/Inf
            exp = 0x3fc00;                    // -> NaN/Inf
        else if( exp != 0 )                   // normalized value
        {
            exp += 0x1c000;                   // exp - 15 + 127
            if( mant == 0 && exp > 0x1c400 )  // smooth transition
                return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
                        | exp << 13 | 0x3ff );
        }
        else if( mant != 0 )                  // && exp==0 -> subnormal
        {
            exp = 0x1c400;                    // make it normal
            do {
                mant <<= 1;                   // mantissa * 2
                exp -= 0x400;                 // decrease exp by 1
            } while( ( mant & 0x400 ) == 0 ); // while not normal
            mant &= 0x3ff;                    // discard subnormal bit
        }                                     // else +/-0 -> +/-0
        return Float.intBitsToFloat(          // combine all parts
                ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
                        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
    }
    // returns all higher 16 bits as 0 for all results
    private int fromFloat( float fval )
    {
        int fbits = Float.floatToIntBits( fval );
        int sign = fbits >>> 16 & 0x8000;          // sign only
        int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

        if( val >= 0x47800000 )               // might be or become NaN/Inf
        {                                     // avoid Inf due to rounding
            if( ( fbits & 0x7fffffff ) >= 0x47800000 )
            {                                 // is or must become NaN/Inf
                if( val < 0x7f800000 )        // was value but too large
                    return sign | 0x7c00;     // make it +/-Inf
                return sign | 0x7c00 |        // remains +/-Inf or NaN
                        ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
            }
            return sign | 0x7bff;             // unrounded not quite Inf
        }
        if( val >= 0x38800000 )               // remains normalized value
            return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
        if( val < 0x33000000 )                // too small for subnormal
            return sign;                      // becomes +/-0
        val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
        return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
                + ( 0x800000 >>> val - 102 )     // round depending on cut off
                >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }
}
