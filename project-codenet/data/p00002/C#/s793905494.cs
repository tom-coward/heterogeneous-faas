using System;


namespace _002_DigitNumber
{
	class Program
	{
		static void Main ( string[] args )
		{
			string inputStr;
			int[] digitCompare = { 0, 
									 10, 
									 100, 
									 1000, 
									 10000, 
									 100000, 
									 1000000, 
									 10000000 };

			while ((inputStr = Console.ReadLine ()) != null)
			{
				string[] split = inputStr.Split (' ');
				int sum = int.Parse (split[0]) + int.Parse (split[1]);
				
				for (int digit = 1; digit <= 7; digit++)
				{
					if (sum < digitCompare[digit])
					{
						Console.WriteLine (digit);
						break;
					}
				}
			}
		}
	}
}