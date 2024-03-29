

package dev.isnow.fox.util;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import dev.isnow.fox.util.type.Pair;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;

import java.util.*;

@UtilityClass
public class MathUtil {


    public final double EXPANDER = Math.pow(2, 24);

    public long elapsed(long time) {
        return Math.abs(System.currentTimeMillis() - time);
    }


    public double getReversedModulus(float div, float a, double remainder) {
        if (a < remainder)
            return (remainder - a);

        return (div + remainder - a);
    }

    public double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        final double average;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static double magnitude(final double... points) {
        double sum = 0.0;

        for (final double point : points) {
            sum += point * point;
        }

        return Math.sqrt(sum);
    }

    public Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getX().add(value);
            }
            else if (value > highThreshold) {
                tuple.getY().add(value);
            }
        }

        return tuple;
    }

    public static double angle(Vector a, Vector b) {
        double dot = Math.min(Math.max(a.dot(b) / (a.length() * b.length()), -1), 1);
        return Math.acos(dot);
    }

    public double preciseRound(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public double getRandomDouble(double number1, double number2) {
        return number1 + (number2 - number1) * new Random().nextDouble();
    }

    public double round(double number, int decimals) {
        number *= Math.pow(10, decimals);
        number = Math.round(number);
        return number / Math.pow(10, decimals);
    }
    public double offset(final Vector a, final Vector b) {
        return a.subtract(b).length();
    }

    public double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);

        return Math.sqrt(variance);
    }

    public double getSkewness(final Collection<? extends Number> data) {
        double sum = 0;
        int count = 0;

        final List<Double> numbers = Lists.newArrayList();

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;

            numbers.add(number.doubleValue());
        }

        Collections.sort(numbers);

        final double mean =  sum / count;
        final double median = (count % 2 != 0) ? numbers.get(count / 2) : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
        final double variance = getVariance(data);

        return 3 * (mean - median) / variance;
    }

    public double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
    }


    public double getAverage(final Collection<? extends Number> data) {
        return data.stream().mapToDouble(Number::doubleValue).average().orElse(0D);
    }

    public double getKurtosis(final Collection<? extends Number> data) {
        double sum = 0.0;
        int count = 0;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        if (count < 3.0) {
            return 0.0;
        }

        final double efficiencyFirst = count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
        final double efficiencySecond = 3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
        final double average = sum / count;

        double variance = 0.0;
        double varianceSquared = 0.0;

        for (final Number number : data) {
            variance += Math.pow(average - number.doubleValue(), 2.0);
            varianceSquared += Math.pow(average - number.doubleValue(), 4.0);
        }

        return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
    }

    public static int getMode(final Collection<? extends Number> array) {
        int mode = (int) array.toArray()[0];
        int maxCount = 0;
        for (final Number value : array) {
            int count = 1;
            for (final Number i : array) {
                if (i.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

    public double getModeDouble(final Double[] data) {
        double maxValue = -1.0d;
        int maxCount = 0;

        for (int i = 0; i < data.length; ++i) {
            final double currentValue = data[i];
            int currentCount = 1;
            for (int j = i + 1; j < data.length; ++j) {
                if (Math.abs(data[j] - currentValue) < 0.001) {
                    ++currentCount;
                }
            }
            if (currentCount > maxCount) {
                maxCount = currentCount;
                maxValue = currentValue;
            } else if (currentCount == maxCount) {
                maxValue = Double.NaN;
            }
        }

        return maxValue;
    }

    public static double getMedian(Iterable<? extends Number> iterable) {
        List<Double> data = new ArrayList<>();

        for (Number number : iterable) {
            data.add(number.doubleValue());
        }

        return getMedian(data);
    }

    public static double getMedian(List<Double> data) {
        if (data.size() > 1) {
            if (data.size() % 2 == 0)
                return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
            else
                return data.get(Math.round(data.size() / 2f));
        }
        return 0;
    }

    public double hypot(double... values) {
        AtomicDouble squaredSum = new AtomicDouble(0D);

        Arrays.stream(values).forEach(value -> squaredSum.getAndAdd(Math.pow(value, 2D)));

        return Math.sqrt(squaredSum.get());
    }

    public float wrapAngleTo180_float(float value) {
        value %= 360F;

        if (value >= 180.0F)
            value -= 360.0F;

        if (value < -180.0F)
            value += 360.0F;

        return value;
    }

    public boolean isExponentiallySmall(final Number number) {
        return Math.abs(number.doubleValue()) < 9E-5;
    }

    public boolean isExponentiallyLarge(final Number number) {
        return Math.abs(number.doubleValue()) > 1E5;
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public double getCps(final Collection<? extends Number> data) {
        return 20 / getAverage(data);
    }

    public int getDuplicates(final Collection<? extends Number> data) {
        return (int)(data.size() - data.stream().distinct().count());
    }

    public int getDistinct(final Collection<? extends Number> data) {
        return (int)data.stream().distinct().count();
    }

    public static Vector getDirection(final float yaw, final float pitch) {
        final Vector vector = new Vector();
        final float rotX = (float)Math.toRadians(yaw);
        final float rotY = (float)Math.toRadians(pitch);
        vector.setY(-Math.sin(rotY));
        final double xz = Math.cos(rotY);
        vector.setX(-xz * Math.sin(rotX));
        vector.setZ(xz * Math.cos(rotX));
        return vector;
    }

    public static float clamp(final float val, final float min, final float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double vectorDist3D(final Vector a, final Vector b) {
        final double xSqrd = Math.pow(Math.abs(a.getX() - b.getX()), 2);
        final double ySqrd = Math.pow(Math.abs(a.getY() - b.getY()), 2);
        final double zSqrd = Math.pow(Math.abs(a.getZ() - b.getZ()), 2);

        return Math.sqrt(xSqrd + ySqrd + zSqrd);
    }

    public static double sortForMin(final ArrayList<Double> al) {
        double min = al.get(0);

        for (int i = 1; i < al.size(); i++) {
            if (al.get(i) < min) {
                min = al.get(i);
            }
        }

        return min;
    }

    public double averageTwoNum(final double a, final double b) {
        return (a + b) / 2;
    }


    public int msToTicks(final double time) {
        return (int) Math.round(time / 50.0);
    }
}
