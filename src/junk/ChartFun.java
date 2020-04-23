package junk;

//
//import static java.util.Locale.ENGLISH ;
//
//import java.awt.FlowLayout ;
//import java.awt.gen.ActionEvent ;
//import java.awt.gen.ActionListener ;
//import java.util.Date ;
//import java.util.HashMap ;
//import java.util.Locale ;
//import java.util.Map ;
//import java.util.TreeSet ;
//
//import javax.swing.JButton ;
//import javax.swing.UIManager ;
//
//import org.jfree.chart.ChartFactory ;
//import org.jfree.chart.ChartPanel ;
//import org.jfree.chart.JFreeChart ;
//import org.jfree.data.time.Millisecond ;
//import org.jfree.data.time.TimeSeries ;
//import org.jfree.data.time.TimeSeriesCollection ;
//import org.jfree.ui.ApplicationFrame ;
//
////import com.juliusbaer.mdp.kernel.price.FxSpot ;
////import com.juliusbaer.mdp.monitoring.Stats ;
//
///** Class for creating charts from time-series data. */
//public class ChartFun
//{
//  private static final long            serialVersionUID = 829741020020682979L ;
//  private static final String          DEFAULT_SERIES   = "default" ;
//
//  private final ApplicationFrame       frame ;
//  private final Map<String,SeriesX> seriesMap        = new HashMap<String,SeriesX>() ;
//  private final JFreeChart             chart ;
//  private boolean                      paused           = false ;
//
//
//  private static class SeriesX
//  {
//    final TimeSeries series ;
//    final Stats stats = new Stats() ;
//
//    SeriesX(String name)
//    {
//      this.series = new TimeSeries(name)  ;
//    }
//
//    void add(long ts, double value)
//    {
//      series.addOrUpdate(new Millisecond(new Date(ts)), value) ;
//      stats.tally(value) ;
//    }
//  }
//
//
//  public ChartFun(String title, int range, String...seriesNames)
//  {
//    if (seriesNames.length == 0)
//      seriesNames = new String[] { DEFAULT_SERIES } ;
//
//
//    try
//    {
//      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel") ;
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace() ;
//    }
//
//    frame = new ApplicationFrame(title) ;
//    frame.setLayout(new FlowLayout()) ;
//
//    TimeSeriesCollection dataset = new TimeSeriesCollection() ;
//
//    for (String sn : seriesNames)
//    {
//      SeriesX series = new SeriesX(sn) ;
//      if (range > 0)
//        series.series.setMaximumItemAge(range * 1000);
//      seriesMap.put(sn, series) ;
//      dataset.addSeries(series.series) ;
//    }
//
//    if (range > 0)
//      System.out.printf("Window range: %ds%n", range) ;
//
//    chart = ChartFactory.createTimeSeriesChart
//    (
//        null,      // chart title
//        null,                      // x axis label
//        null,                      // y axis label
//        dataset,                  // data
//        true,                     // include legend
//        true,                     // tooltips
//        true // urls
//    );
//
//    chart.setAntiAlias(true) ;
//
//    ChartPanel chartPanel = new ChartPanel(chart);
//    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//
//    // setup and go
//    frame.setContentPane(chartPanel);
//    frame.add(new PauseButton()) ;
//    frame.pack() ;
//    frame.setVisible(true) ;
//  }
//
//  {
//    new Thread()
//    {
//      @Override
//      public void run()
//      {
//        while(true)
//        {
//          System.out.println("\n") ;
//          for (Object sk : new TreeSet<String>(seriesMap.keySet()))
//            System.out.printf("%s - %s%n", sk, seriesMap.get(sk).stats) ;
//          MDPUtil.sleep(5000) ;
//        }
//      }
//    }.start() ;
//  }
//
//  public void add(long ts, double value)
//  {
//    add(DEFAULT_SERIES, ts, value) ;
//  }
//
//  public void add(String name, long ts, double value)
//  {
//    if (!paused)
//      seriesMap.get(name).add(ts, value) ;
//  }
//
//
//  public void stdinLoop()
//  {
//    double last = NULL ;
//
//    Window<Double> window = new Window<Double>(5) ;
//
//    System.out.println("Reading from <stdin>") ;
//    for (String line : new LineReader(System.in))
//    {
//      try
//      {
////        String[] toks = line.split(" +") ;
////        long ts = DATE_FORMAT.parseDateTime(toks[0] + " " + toks[1]).getMillis() ;
////        double value = Double.parseDouble(toks[2]) ;
//
////        SimpleMarketData md = SimpleLogParser.parse(line) ;
//
//        FxSpot md = LegacyLogParser.parseSpotState(line) ;
//
//        if (last == NULL)
//        {
//          last = md.ask() ;
//          continue ;
//        }
//
//        double move = Math.abs(MDPMath.priceMove(last, md.ask())) ;
//        add("move", md.sbTimestamp(), move) ;
//
//        window.add(move) ;
//
//        Stats stats = new Stats() ;
//        double avg = 0 ;
//        for (Double d : window)
//        {
//          stats.tally(d) ;
//          avg += d / window.n() ;
//        }
//
//        add("ma", md.sbTimestamp(), avg) ;
//        add("diff", md.sbTimestamp(), move - avg) ;
//        add("std-dev", md.sbTimestamp(), stats.stdDev()) ;
//
//
////        add("bid", md.sbTimestamp(), md.bid()) ;
////        add("ask", md.sbTimestamp(), md.ask()) ;
//      }
//      catch (Exception e)
//      {
//        System.err.println(e + ": " + line) ;
//      }
//    }
//    System.out.println("Finished reading") ;
//  }
//
//
//  public static void main(String[] args)
//  {
//    Locale.setDefault(ENGLISH) ;
//
//    int range = 0 ;
//    if (args.length > 1)
//    {
//      if (args[0].equals("-r"))
//        range = Integer.parseInt(args[1]) ;
//    }
//
//    new ChartFun("Chart", range, "move", "ma", "diff", "std-dev").stdinLoop() ;
//  }
//
//
//  private class PauseButton extends JButton
//  {
//    private static final long serialVersionUID = 7010855753177667291L ;
//
//    PauseButton()
//    {
//      super("Pause") ;
//      addActionListener(new ActionListener()
//      {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//          PauseButton.this.setText((paused =! paused) ? "Unpause" : "Pause") ;
//          System.out.println("paused = " + paused) ;
//        }
//      }) ;
//    }
//  }
//}
