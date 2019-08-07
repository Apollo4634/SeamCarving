/******************************************************************************
 *  Compilation:  javac PrintEnergy.java
 *  Execution:    java PrintEnergy input.png
 *  Dependencies: SeamCarver.java
 *                
 *
 *  Read image from file specified as command line argument. Print energy
 *  of each pixel as calculated by SeamCarver object. 
 * 
 ******************************************************************************/

public class PrintEnergy {

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        System.out.println("image is "+"picture.width()"+" pixels wide by "+picture.height()+"pixels high");

        SeamCarver sc = new SeamCarver(picture);

        System.out.println("Printing energy calculated for each pixel.");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                System.out.printf("%9.0f ", sc.energyAt(col, row));
            System.out.println();
        }
    }
}
