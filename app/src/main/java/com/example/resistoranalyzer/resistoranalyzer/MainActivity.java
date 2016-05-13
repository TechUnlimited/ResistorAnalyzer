package com.example.resistoranalyzer.resistoranalyzer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;




public class MainActivity extends AppCompatActivity {
    public static final String TAG = "RV";
    EditText inputBox;

    int firstBand;
    int secondBand;
    float multiplierBand =0;

    String mulString ="";

    public enum color {
        BLACK,
        BROWN,
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        VIOLET,
        GREY,
        WHITE
    }
    color firstBandColor, secondBandColor;

    public enum multiplierColor {
        BLACK,
        BROWN,
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        GOLD,
        SILVER
    }
    multiplierColor multiplierBandColor;

    public enum ErrorCode {
        NOT_A_VALID_INPUT,
        NOT_A_VALID_RESISTOR,
        RESISTOR_VALUE_ZERO,
        RESISTOR_VALUE_CANT_REPRESENT,
        MULTIPLIER_OTHER_THAN_ZERO_M_OR_K,
        EXCEEDED_VALID_NUMBER_OF_DIGIT,
        FLOAT_OUT_OF_RANGE,
        NO_ERROR
    }
    ErrorCode Er;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "On Create");
        inputBox = (EditText) findViewById(R.id.txtInputResistorValue);

    }

    public void onSubmitBtnClicked(View view) {
        //int i = Integer.parseInt(inputBox.getText().toString());
        parseInputValue(inputBox.getText().toString());
        //parseInputValue(i);

    }

    public void parseInputValue(String inputValue) {

        int length = inputValue.length();
        ErrorCode er = null;

        er = validateInput(inputValue, length);

        if(er != er.NO_ERROR){
            Log.d(TAG, "Following Error occurred " + er);
            Log.d(TAG, "USAGE:" + "\n" +
                        "1:: 1200000" + "\n" +
                        "2:: 12M/K" + "\n" +
                        "3:: 1.2" + "\n" +
                        "4:: 12 M/K");
            return;
        }
        if((inputValue.charAt(length-1)== 'K' )|| (inputValue.charAt(length-1)== 'M' )){
            if(inputValue.indexOf(".") != -1 ){
                //int i = inputValue.indexOf(".");
                Log.d(TAG, " User Entrered float value with Multiplier" );
            }
            else{
                Log.d(TAG, " User Entrered Integer value with Multiplier");
                integerMultiplierInput(inputValue , length);
            }

        }
        else if(inputValue.indexOf(".") != -1 ){
            Log.d(TAG, " User Entrered float value without Multiplier");
            floatInput(inputValue, length);
        }
        else{
            Log.d(TAG, " User Entrered pure Integer value ");
            integerInput(inputValue, length);
        }
        calculateBands();
    }



    public void calculateBands(){

        firstBandColor = findColor(firstBand);
        Log.d(TAG, "First Band Color::" + firstBandColor );

        secondBandColor = findColor(secondBand);
        Log.d(TAG, "Second Band Color::" + secondBandColor);

        multiplierBandColor = findMultuplierColor( mulString);
        Log.d(TAG, "Multiplier Band Color::" + multiplierBandColor);

    }

    public void integerMultiplierInput(String inputValue, int length){

        //Function Will Set the First and second Band
        integerInput(inputValue.substring(0,length-1), length-1);

        //Preparation of Multiplier String to get the Enum value.
        if( inputValue.charAt(length-1)== 'K' ){
            mulString = multiplierBand +"K";
            Log.d(TAG,mulString );
        }
        else if (inputValue.charAt(length-1)== 'M' ){
            mulString = multiplierBand +"M";
            Log.d(TAG,mulString );
        }
    }

    public void integerInput(String inputValue, int length){
        int integerInput = Integer.parseInt(inputValue);
        int icount = 0;

        //***Special Case handles the 1 digit integer input
        if (length == 1 && inputValue.charAt(0)!= '0' ) {
            //Calculation of first Band
            firstBand = 0 ;
            secondBand = integerInput;
            multiplierBand = 1;
            mulString = multiplierBand + "";
            return;
        }

        //Calculation of first Band
        firstBand = (int) (integerInput / Math.pow(10,length-1));

        //Calculation of Second Band
        secondBand = (int) ((integerInput - (firstBand * Math.pow(10,length-1) ) ) / Math.pow(10,length-2)) ;

        //Calculation of Multiplier Band
        for( icount = 0 ;integerInput % 10 == 0 ;icount++, integerInput = integerInput / 10 )
            ;
        multiplierBand = (int) Math.pow(10,icount);
        if(icount >= 3){
            if(icount ==3){
                Log.d(TAG, "iCount is 3 and it is equivalent to K");
                mulString = "1.0K"; //Float values are to make compatible with float input
                Log.d(TAG, "MulStr" +mulString );
                return;
            }
            else if(icount ==4){
                mulString = "10.0K"; //Float values are to make compatible with float input
                return;
            }
            else if(icount ==5){
                mulString = "100.0K"; //Float values are to make compatible with float input
                return;
            }
            else if(icount == 6){
                mulString =  "1.0M"; //Float values are to make compatible with float input
                return;
            }
            return;
        }
        mulString = multiplierBand + "";
        return;

    }

    public void floatInput(String inputValue, int length){
        int iCount;
        Log.d(TAG,"entered float ");
        if(length > 4 ){
            Log.d(TAG,"Float Exceeding the valid range " + Er.NOT_A_VALID_INPUT);
        }

        float floatInput = Float.parseFloat(inputValue);
        Log.d(TAG,"Float value input ::" + floatInput );

        Log.d(TAG,"Floor value of the Float::" + (Math.round(floatInput) ));
        Log.d(TAG,"10 ***::" + Math.round(floatInput*10) );
        Log.d(TAG,"100 ***::" + Math.round(floatInput*100) );
        Log.d(TAG,"Subtract Float::" + (floatInput- (Math.floor(floatInput) )));

        for( iCount = 0 ;(floatInput*10) % 1 != 0 ;iCount++ ){
            Log.d(TAG,"Float value in for loop ::" + (Math.floor(floatInput*10) % 1));
            Log.d(TAG,"iCOunt::" + iCount );
            floatInput= floatInput*10;
        }

        Log.d(TAG,"Float value out for loop ::" + (Math.floor(floatInput*10) % 1));

        String strFloat = floatInput+ "";
       // integerInput(strFloat, strFloat.length());

        multiplierBand = (float) (1/ Math.pow(10,iCount));
        mulString = multiplierBand + "";
        return;
    }


    /**
     * Function does the parse the input for any error, ensured that user entered the value correctly
     * @param inputValue
     * @param length
     * @return Enum Error codes
     */
    public ErrorCode validateInput(String inputValue,int length) {

        // User entered nothing
        if (length < 1) {
            return Er.NOT_A_VALID_INPUT;
        }
        //Entered Value is too Long to represent
        if (length > 8) {
            return Er.RESISTOR_VALUE_CANT_REPRESENT;
        }

        //User entered the invalid Resistor. Both band cannot be Zero.
        if (length == 1 && inputValue.charAt(0) == '0') {
            return Er.RESISTOR_VALUE_ZERO;
        }

        //User entered the invalid Resistor. Both band cannot be Zero.
        if ( inputValue.charAt(0) == '0' && inputValue.charAt(1) == '0' ) {
            return Er.NOT_A_VALID_RESISTOR;
        }

        //User entered the invalid Resistor. Both band cannot be Zero.
        if ( length == 3 && (inputValue.charAt(2) != '0' && inputValue.charAt(length-1) != 'K' && inputValue.charAt(length-1) != 'M')
                && (inputValue.indexOf(".") == -1 )){
            return Er.MULTIPLIER_OTHER_THAN_ZERO_M_OR_K;
        }

        //User entered the invalid value such as 12000K.instead of 1200K
        // 5 is the count for valid digits such as 1200K
        if(( (inputValue.charAt(length-1)== 'K' ) && (inputValue.indexOf(".") == -1 ) && length > 5 )){
            return Er.EXCEEDED_VALID_NUMBER_OF_DIGIT;
        }

        //User entered the invalid value such as 1200M.instead of 12M
        // 3 is the count for valid digits such as 12M
        if(((inputValue.charAt(length-1)== 'M' ) &&(inputValue.indexOf(".") == -1 ) && length > 3 )){
            return Er.EXCEEDED_VALID_NUMBER_OF_DIGIT;
        }

        //User entered the invalid value such as 1200M.instead of 12M
        // 3 is the count for valid digits such as 12M
        if(((inputValue.charAt(length-1)== 'M' ) &&  (inputValue.charAt(length-1)== 'K' )) && (inputValue.indexOf(".") >3 )){
            return Er.FLOAT_OUT_OF_RANGE;
        }

        return Er.NO_ERROR;
    }


    /**
     *
     * @param Band denotes First and second digit of Resistor
     * @return color of resistir first and Second band
     */
    public color findColor(int Band) {
        color bandcolor = null;

        switch (Band) {
            case 0:
                bandcolor = color.BLACK;
                break;
            case 1:
                bandcolor = color.BROWN;
                break;
            case 2:
                bandcolor = color.RED;
                break;
            case 3:
                bandcolor = color.ORANGE;
                break;
            case 4:
                bandcolor = color.YELLOW;
                break;
            case 5:
                bandcolor = color.GREEN;
                break;
            case 6:
                bandcolor = color.BLUE;
                break;
            case 7:
                bandcolor = color.VIOLET;
                break;
            case 8:
                bandcolor = color.GREY;
                break;
            case 9:
                bandcolor = color.WHITE;
                break;
            default:
                Log.d(TAG, "No MAtching color");
        }
        return bandcolor;
    }

    /**
     *
     * @param Band denotes the band of Multiplier. takes input in string format
     * @return returns the Multiplier color
     */
    public multiplierColor findMultuplierColor(String Band) {
        multiplierColor multiplierBandColor = null;

        switch (Band) {
            case "1.0":
                multiplierBandColor = multiplierColor.BLACK;
                break;
            case "10.0":
                multiplierBandColor = multiplierColor.BROWN;
                break;
            case "100.0":
                multiplierBandColor = multiplierColor.RED;
                break;
            case "1.0K":
                multiplierBandColor = multiplierColor.ORANGE;
                break;
            case "10.0K":
                multiplierBandColor = multiplierColor.YELLOW;
                break;
            case "100.0K":
                multiplierBandColor = multiplierColor.GREEN;
                break;
            case "1.0M":
                multiplierBandColor = multiplierColor.BLUE;
                break;
            case "0.1":
                multiplierBandColor = multiplierColor.GOLD;
                break;
            case "0.01":
                multiplierBandColor = multiplierColor.SILVER;
                break;
            default:
                Log.d(TAG, "No MAtching color");
        }
        return multiplierBandColor;

    }
}

