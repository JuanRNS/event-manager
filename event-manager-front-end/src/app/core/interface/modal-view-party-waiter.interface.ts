
export interface IResponseModalViewPartyWaiter {
    name: string;
    phone: string;
    pixKey: string;
    parties: IPartyByWaiter[];
}

export interface IPartyByWaiter {
    location: string;
    nameClient: string;
    date: string;
    hourStart: string;
    hourEnd: string;
    valuePerDay: number;
    status: string;
    numberOfPeople: number;
}