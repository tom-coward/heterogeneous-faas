using System;
class _0002
{
    public static void Main()
    {
        const int dataNum = 200;
        char[] splitChar = {' '};
        int[] a = new int[dataNum];
        int[] b = new int[dataNum];
        int k = 0;
        string str;
        while((str = Console.ReadLine()) != null){
            string[] nums = str.Split(splitChar);
            a[k] = int.Parse(nums[0]);
            b[k] = int.Parse(nums[1]);
            k++;
        }
        for (int i = 0; i < k; i++)
        {
            int sum = a[i] + b[i];
            Console.WriteLine(sum.ToString().Length);
        }
    }
}