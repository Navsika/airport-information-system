import { airport } from '../services/airport';
import { airline } from '../services/airline';
import { aircraftModel } from '../services/aircraftModel';
import { aircraft } from '../services/aircraft';
import { employee } from '../services/employee';
import { passenger } from '../services/passenger';

export const REFERENCES = {
  airports: {
    title: 'Аэропорты',
    service: airport,
    id: 'airportId',
    columns: [
      ['iataCode', 'IATA'],
      ['airportName', 'Название'],
      ['city', 'Город'],
      ['country', 'Страна'],
    ],
    facets: [
      { type: 'text', field: 'iataCode', label: 'IATA', placeholder: 'Например, SVO' },
      { type: 'value-select', field: 'city', label: 'Город', sourceField: 'city' },
      { type: 'value-select', field: 'country', label: 'Страна', sourceField: 'country' },
    ],
    fields: [
      ['iataCode', 'IATA', 'text', null, 'Например, SVO'],
      ['airportName', 'Название', 'text', null, 'Например, Шереметьево'],
      ['city', 'Город', 'value-select', 'city', 'Например, Москва'],
      ['country', 'Страна', 'value-select', 'country', 'Например, Россия'],
    ],
  },
  airlines: {
    title: 'Авиакомпании',
    service: airline,
    id: 'airlineId',
    columns: [
      ['iataCode', 'IATA'],
      ['airlineName', 'Название'],
      ['country', 'Страна'],
    ],
    facets: [
      { type: 'text', field: 'iataCode', label: 'IATA', placeholder: 'Например, S7' },
      { type: 'text', field: 'airlineName', label: 'Название', placeholder: 'Например, S7 Airlines' },
      { type: 'value-select', field: 'country', label: 'Страна', sourceField: 'country' },
    ],
    fields: [
      ['iataCode', 'IATA', 'text', null, 'Например, S7'],
      ['airlineName', 'Название', 'text', null, 'Например, S7 Airlines'],
      ['country', 'Страна', 'value-select', 'country', 'Например, Россия'],
    ],
  },
  models: {
    title: 'Модели ВС',
    service: aircraftModel,
    id: 'modelId',
    columns: [
      ['modelName', 'Модель'],
      ['manufacturer', 'Производитель'],
      ['passengerCapacity', 'Пассажиры'],
      ['cargoCapacity', 'Груз, кг'],
      ['maxSpeed', 'Скорость, км/ч'],
    ],
    facets: [
      { type: 'text', field: 'modelName', label: 'Модель', placeholder: 'Например, Superjet 100' },
      { type: 'value-select', field: 'manufacturer', label: 'Производитель', sourceField: 'manufacturer' },
      { type: 'range', field: 'passengerCapacity', label: 'Пассажиры' },
      { type: 'range', field: 'cargoCapacity', label: 'Груз, кг' },
      { type: 'range', field: 'maxSpeed', label: 'Скорость, км/ч' },
    ],
    fields: [
      ['modelName', 'Модель', 'text', null, 'Например, Superjet 100'],
      ['manufacturer', 'Производитель', 'value-select', 'manufacturer', 'Например, Sukhoi'],
      ['passengerCapacity', 'Пассажиры', 'number', null, 'Например, 98'],
      ['cargoCapacity', 'Груз, кг', 'number', null, 'Например, 4500'],
      ['maxSpeed', 'Скорость', 'number', null, 'Например, 870'],
    ],
  },
  aircrafts: {
    title: 'Самолеты',
    service: aircraft,
    id: 'aircraftId',
    lookups: ['models', 'airlines'],
    columns: [
      ['registrationNumber', 'Бортовой номер'],
      ['modelId', 'Модель'],
      ['airlineId', 'Авиакомпания'],
      ['manufactureYear', 'Год'],
      ['flightHours', 'Налет'],
    ],
    facets: [
      { type: 'lookup-select', field: 'modelId', label: 'Модель', lookup: 'models' },
      { type: 'lookup-select', field: 'airlineId', label: 'Авиакомпания', lookup: 'airlines' },
      { type: 'number', field: 'manufactureYear', label: 'Год выпуска', placeholder: 'Например, 2020' },
      { type: 'date', field: 'lastMaintenanceDate', label: 'Дата ТО' },
      { type: 'number', field: 'flightHours', label: 'Налет', placeholder: 'Например, 1240' },
    ],
    fields: [
      ['registrationNumber', 'Бортовой номер', 'text', null, 'Например, RA-89001'],
      ['modelId', 'Модель', 'lookup-select', 'models', 'Например, Superjet'],
      ['airlineId', 'Авиакомпания', 'lookup-select', 'airlines', 'Например, S7'],
      ['manufactureYear', 'Год выпуска', 'number', null, 'Например, 2020'],
      ['lastMaintenanceDate', 'Последнее ТО', 'date'],
      ['flightHours', 'Налет', 'number', null, 'Например, 1240'],
    ],
  },
  employees: {
    title: 'Сотрудники',
    service: employee,
    id: 'employeeId',
    columns: [
      ['lastName', 'Фамилия'],
      ['firstName', 'Имя'],
      ['middleName', 'Отчество'],
      ['category', 'Категория'],
      ['hireDate', 'Дата найма'],
    ],
    facets: [
      { type: 'text', field: 'lastName', label: 'Фамилия', placeholder: 'Например, Иванов' },
      { type: 'choice', field: 'category', label: 'Категория', options: [['Pilot', 'Пилот'], ['FlightAttendant', 'Бортпроводник']] },
      { type: 'date-range', field: 'hireDate', label: 'Дата найма' },
    ],
    fields: [
      ['lastName', 'Фамилия', 'text', null, 'Например, Иванов'],
      ['firstName', 'Имя', 'text', null, 'Например, Иван'],
      ['middleName', 'Отчество', 'text', null, 'Например, Иванович'],
      ['category', 'Категория', 'select', ['Pilot', 'FlightAttendant']],
      ['hireDate', 'Дата найма', 'date'],
    ],
  },
  passengers: {
    title: 'Пассажиры',
    service: passenger,
    id: 'passengerId',
    columns: [
      ['lastName', 'Фамилия'],
      ['firstName', 'Имя'],
      ['middleName', 'Отчество'],
      ['passportNumber', 'Паспорт'],
      ['passportExpiryDate', 'Действует до'],
    ],
    facets: [
      { type: 'text', field: 'lastName', label: 'Фамилия', placeholder: 'Например, Петрова' },
      { type: 'text', field: 'firstName', label: 'Имя', placeholder: 'Например, Анна' },
    ],
    fields: [
      ['lastName', 'Фамилия', 'text', null, 'Например, Петрова'],
      ['firstName', 'Имя', 'text', null, 'Например, Анна'],
      ['middleName', 'Отчество', 'text', null, 'Например, Сергеевна'],
      ['passportNumber', 'Паспорт', 'text', null, 'Например, 4012 345678'],
      ['passportExpiryDate', 'Действует до', 'date'],
    ],
  },
};
