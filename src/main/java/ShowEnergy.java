
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
