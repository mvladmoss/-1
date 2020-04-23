import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Anaglyph {

    private static final double[][][] COLOR_MATRIX = {{{1, 0, 0}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 1, 0}, {0, 0, 1}}};

    private static final double[][][] HALF_COLOR_MATRIX = {{{0.229, 0.587, 0.114}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 1, 0}, {0, 0, 1}}};

    private static final double[][][] OPTIMIZED_MATRIX = {{{0, 0.45, 1.05}, {0, 0, 0}, {0, 0, 0}},
            {{0, 0, 0}, {0, 1, 0}, {0, 0, 1}}};


    public static void main(String[] args) throws IOException {
        File left = new File("/Users/vladmoss/Desktop/labs/Images/images/left.jpg");
        File right = new File("/Users/vladmoss/Desktop/labs/Images/images/right.jpg");

        BufferedImage leftImage = ImageIO.read(left);
        BufferedImage rightImage = ImageIO.read(right);
        File output = new File("/Users/vladmoss/Desktop/labs/Images/images/FINAL.jpg");
        BufferedImage resultImage = new BufferedImage(leftImage.getWidth(), leftImage.getHeight(), BufferedImage.TYPE_USHORT_555_RGB);

        for (int i = 0; i < leftImage.getWidth(); i++) {
            for (int j = 0; j < leftImage.getHeight(); j++) {
                Color leftImageRGB = new Color(leftImage.getRGB(i, j));
                double[] leftArrayRGB = {leftImageRGB.getRed(), leftImageRGB.getGreen(), leftImageRGB.getBlue()};

                Color rightImageRGB = new Color(rightImage.getRGB(i, j));
                double[] rightArrayRGB = {rightImageRGB.getRed(), rightImageRGB.getGreen(), rightImageRGB.getBlue()};

                SimpleMatrix leftRGB = new SimpleMatrix(1, leftArrayRGB.length);
                leftRGB.setRow(0, 0, leftArrayRGB);
                SimpleMatrix rightRGB = new SimpleMatrix(1, rightArrayRGB.length);
                rightRGB.setRow(0, 0, rightArrayRGB);

                SimpleMatrix newRGB = createMatrixFromVector(createNewRGB(OPTIMIZED_MATRIX[0], leftArrayRGB)).plus(
                        createMatrixFromVector(createNewRGB(OPTIMIZED_MATRIX[1], rightArrayRGB))
                );

                resultImage.setRGB(i, j, new Color((int) newRGB.get(0, 0), (int) newRGB.get(0, 1),
                        (int) newRGB.get(0, 2)).getRGB());
            }
        }
        ImageIO.write(resultImage,"jpg",output);
    }

    private static double[] createNewRGB(double[][] predefinedMatrix, double[] imageVectorRGB) {
        double[] vectorRGB = new double[3];
        double R = createMatrixFromVector(predefinedMatrix[0]).dot(createMatrixFromVector(imageVectorRGB));
        double G = createMatrixFromVector(predefinedMatrix[1]).dot(createMatrixFromVector(imageVectorRGB));
        double B = createMatrixFromVector(predefinedMatrix[2]).dot(createMatrixFromVector(imageVectorRGB));
        vectorRGB[0] = R > 255 ? 255 : R;
        vectorRGB[1] = G > 255 ? 255 : G;
        vectorRGB[2] = B > 255 ? 255 : B;
        return vectorRGB;
    }

    private static SimpleMatrix createMatrixFromVector(double[] vector) {
        SimpleMatrix matrix = new SimpleMatrix(1, vector.length);
        matrix.setRow(0, 0, vector);
        return matrix;
    }
}

