/******************************************************************************
 *  Compilation:  javac ShowEnergy.java
 *  Execution:    java ShowEnergy input.png
 *  Dependencies: SeamCarver.java SCUtility.java
 *                
 *
 *  Read image from file specified as command line argument. Show original
 *  image (only useful if image is large enough).
 *
 ******************************************************************************/


public class ShowEnergy {

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        System.out.printf("image is %d columns by %d rows\n", picture.width(), picture.height());
        picture.show();        
        SeamCarver sc = new SeamCarver(picture);

        System.out.println("Displaying energy calculated for each pixel.");
        SCUtility.showEnergy(sc);
    }
}
