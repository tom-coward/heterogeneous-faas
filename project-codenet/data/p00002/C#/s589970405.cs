using System;
public class Program {
  public static void Main() {
    var input = "";
    while(!string.IsNullOrEmpty((input = Console.ReadLine()))) {  
      var values = input.Split(' ');
      int a = int.Parse(values[0]);
      int b = int.Parse(values[1]);
      Console.WriteLine((a + b).ToString().Length);
    }
  }
}