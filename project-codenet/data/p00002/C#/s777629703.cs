using System;

namespace _0002 {

	public static class Program {

		public static void Main() {
			string input;
			while (!string.IsNullOrEmpty(input = Console.ReadLine())) {
				var inarray = input.Split(' ');
				Console.WriteLine((int.Parse(inarray[0]) + int.Parse(inarray[1])).ToString().Length);
			}
		}

	}

}