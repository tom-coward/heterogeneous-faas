using System;
class main{
    static void Main(string[] args){
        for (int i = 0; i < 200;i++ ){
            string input = Console.ReadLine();
            if(!String.IsNullOrEmpty(input)){
                string[] data = input.Split(' ');
                Console.WriteLine((int.Parse(data[0] )+ int.Parse(data[1])).ToString().Length);
            }else{
                break;
            }
        }
    }
}