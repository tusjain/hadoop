
package testing.withunittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordCountUtils {
  private static final Logger LOG = LoggerFactory.getLogger(WordCountUtils.class);

  public static String[] splitStr(String strToSplit){
    String[] split = strToSplit.split(" ");
    return split;
  }

  public static int countValues(Iterable values) {
    int sum = 0;
    for (Object obj:values){
      sum++;
    }
    return sum;
  }
}
