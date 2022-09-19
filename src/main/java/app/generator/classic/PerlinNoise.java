package app.generator.classic;

import app.utility.MathUtil;

public class PerlinNoise {

    public PerlinNoise(){}

    /**@param squareSize A power of two representing the size of the target image.
     * @param numOfOctaves Inputting a int N will generate octaves starting from 1 to N of sizes corresponding to their order.*/
    public float[][] generateNoiseMap(int squareSize, int numOfOctaves){
        int sizeInPixels = 1 << squareSize; // Same as: 2^squareSize
        numOfOctaves = (numOfOctaves <= squareSize) ? numOfOctaves : squareSize;

        System.out.printf("Generating perlin noise.[SIZE:%d; OCTAVES:%d]\n", sizeInPixels, numOfOctaves);

        float[][] noiseMap = new float[sizeInPixels][sizeInPixels];

        //Initialising random octaves
        Octave octaves[] = new Octave[numOfOctaves];
        for(int i=0; i<numOfOctaves; i++){
            octaves[i] = new Octave(i,sizeInPixels);
            octaves[i].generateOctave(true);
        }

        //Blend the octaves together
        float persistance = 0.38f;
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        //Loop trough each octave and add the weighted value
        for(int oct = numOfOctaves-1; oct >= 0; oct--){

            for(int j=0;j<sizeInPixels;j++)
                for(int i=0;i<sizeInPixels;i++)
                    noiseMap[j][i] += octaves[oct].data[j][i] * amplitude;

            totalAmplitude += amplitude;
            amplitude *= persistance;
        }

        //Normalize the values of the noise map
        for(int j=0;j<sizeInPixels;j++){
            for(int i=0;i<sizeInPixels;i++){
                noiseMap[j][i] /= totalAmplitude;
            }
        }

        return noiseMap;
    }

    /**Each octave consists of random noise, generated at different scales.
     * The higher the size of the octave, the more that octave will contribute to the overall detail of the final noise.
     * */
    class Octave{

        float[][] data;
        final int waveLength, sizeInPixels;
        final float frequency;

        /**@param octavePeriod A power of 2 representing an interval at which random values are generated and interpolated in between.
         * @param absoluteSize The size of the final image.
         * */
        public Octave(int octavePeriod, int absoluteSize){
            this.sizeInPixels = absoluteSize;
            data = new float[absoluteSize][absoluteSize];

            waveLength = 1 << octavePeriod;
            frequency = 1.0f/(float)waveLength;
        }

        public void generateOctave(boolean smoothInterpolation) {
            //Initialise octave with random values at period points
            for (int y = 1; y <= sizeInPixels; y += waveLength)
                for (int x = 1; x <= sizeInPixels; x += waveLength)
                    data[y-1][x-1] = (float) Math.random();

            //Bi-linear interpolation
            for (int y = 0; y < sizeInPixels; y++) {
                //Calculate vertical sampling indices
                int y0 = y - (y % waveLength);
                int y1 = (y0 + waveLength) % sizeInPixels;
                float verticalBlend = (y % waveLength) * frequency;

                if(smoothInterpolation)
                    verticalBlend = smooth_function(verticalBlend);

                for (int x = 0; x < sizeInPixels; x++) {
                    //Calculate horizontal sampling indices
                    int x0 = x - (x % waveLength);
                    int x1 = (x0 + waveLength) % sizeInPixels;
                    float horizontalBlend = (x % waveLength) * frequency;

                    if(smoothInterpolation)
                        horizontalBlend = smooth_function(horizontalBlend);

                    //Horizontal interpolation for upper and lower bounds (for vertical interpolation)
                    float top = MathUtil.interpolate(data[y0][x0], data[y0][x1], horizontalBlend);
                    float bottom = MathUtil.interpolate(data[y1][x0], data[y1][x1], horizontalBlend);

                    //Vertical interpolation
                    data[y][x] = MathUtil.interpolate(top, bottom, verticalBlend);
                }
            }
        }

        //The scope of this function is to help get rid of grid artifacts when generating perlin noise
        private float smooth_function(float x){
            return 6*(float)Math.pow(x, 5)-15*(float)Math.pow(x, 4)+10*(float)Math.pow(x, 3);
        }

        public String toString(){
            return String.format("[Wave length: %d; Size: %d]", waveLength, sizeInPixels);
        }

    }//end of octave class

}//end of class
