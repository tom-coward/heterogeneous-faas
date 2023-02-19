using System;
using System.Linq;
public class Program {
	public static void Main() {
		string buf;
		while(!string.IsNullOrEmpty(buf = Console.ReadLine())) {
			Console.WriteLine(buf.Split().Select(int.Parse).Sum().ToString().Length);
		}
	}
}