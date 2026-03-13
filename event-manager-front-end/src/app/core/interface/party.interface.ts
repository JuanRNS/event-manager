import { IEmployeeType, IPage, IResponseMaterial } from "./event.interface";

export interface IResponseParty {
  id: number;
  location: string;
  nameClient: string;
  date: string;
  material: IResponseMaterial;
  employeeId: number[];
  values: IResponseValues[];
  numberOfPeople: number;
  status: string;
}

export interface IResponseValues {
  id: number;
  employeeType: IEmployeeType;
  value: number;
}

export interface IRequestParty {
  location: string;
  nameClient: string;
  date: string;
  idMaterial: number;
  numberOfPeople: number;
  hourStart: string;
  hourEnd: string;
}

export interface IResponseListParty {
  content: IResponseParty[];
  page: IPage;
}