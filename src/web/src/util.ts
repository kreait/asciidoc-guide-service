export default class Util {

    public static chunk<T>(arr: T[], chunkSize: number): T[][] {
        return arr.reduce((prevVal: any, currVal: any, currIndx: number, array: T[]) =>
            !(currIndx % chunkSize) ?
                prevVal.concat([array.slice(currIndx, currIndx + chunkSize)]) :
                prevVal, []);
    }
}
