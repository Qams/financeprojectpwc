import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {apiUrl, httpOptions} from '../utils/api';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {Transaction, Transfer} from '../dataModel/transaction';
import {ContentType} from '@angular/http/src/enums';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  public accessToken: string;
  public username: string;
  public userId: number;

  constructor(private http: HttpClient,
              private router: Router) {
    this.accessToken = localStorage.getItem('accessToken');
    this.username = localStorage.getItem('username');
    this.userId = Number(localStorage.getItem('userId'));
  }

  saveLoginData(token: string, login: string): Promise<any> {
    this.accessToken = token;
    this.username = login;
    localStorage.setItem('accessToken', token);
    localStorage.setItem('username', login);

    return this.getUserByUsername(this.username)
      .then(response => {
        this.userId = response.id;
        localStorage.setItem('userId', String(this.userId));
        localStorage.setItem('firstname', String(response.firstName));
        localStorage.setItem('surname', String(response.surname));
        localStorage.setItem('accountList', JSON.stringify(response.accounts));
        localStorage.setItem('currentAccount', JSON.stringify(response.accounts[0]));
        localStorage.setItem('role', String(response.role));
      });
  }

  login(login: string, password: string): Promise<any> {
    return this.http.post<any>(apiUrl + '/oauth/token', `username=${login}&password=${password}&grant_type=password`, httpOptions).toPromise();
  }

  register(login: string, password: string): Promise<any> {
    return this.http.post<any>(apiUrl + '/users/register', {login, password}).toPromise();
  }

  getUserByUsername(username: string): Promise<any> {
    return this.http.get<any>(apiUrl + '/users/name/' + username + '?access_token=' + this.accessToken).toPromise();
  }

  getPaysimFraud(): Observable<any> {
    return this.http.get<any>(apiUrl + '/paysim/fraud?' + 'access_token=' + this.accessToken);
  }

  getCurrentExchangeRates(): Observable<any> {
    return this.http.get<any>(apiUrl + '/exchange/all/current?' + 'access_token=' + this.accessToken);
  }

  getCurrentExchangeOpenRates(): Observable<any> {
    return this.http.get<any>(apiUrl + '/exchange/open' + '?access_token=' + this.accessToken);
  }

  getUserById(id: number): Promise<any> {
    return this.http.get<any>(apiUrl + '/users/' + id + '?access_token=' + this.accessToken).toPromise();
  }

  getUsers(page: number): Promise<any> {
    return this.http.get<any>(apiUrl + '/users?page=' + page).toPromise();
  }

  getCurrentUser(): Promise<any> {
    return this.http.get<any>(apiUrl + '/users/' + this.userId + '?access_token=' + this.accessToken).toPromise();
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('accessToken') !== null;
  }

  getCurrentAccount(): string {
    const account = localStorage.getItem('currentAccount');
    if(account) {
      return JSON.parse(account);
    }
  }

  getCurrentAccountNumber(): string {
    const account = localStorage.getItem('currentAccount');
    if(account) {
      return JSON.parse(account).number;
    }
  }

  getCurrentAccountCurrency(): string {
    const account = localStorage.getItem('currentAccount');
    if(account) {
      return JSON.parse(account).currency;
    }
  }

  getAccounts() {
    let accounts = localStorage.getItem('accountList');
    if(accounts) {
      return JSON.parse(accounts);
    }
  }

  getFirstName(): string {
    return localStorage.getItem('firstname');
  }

  getSurname(): string {
    return localStorage.getItem('surname');
  }

  getRole(): string {
    return localStorage.getItem('role');
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  getTransactions(accountNumber: string): Observable<any> {
    return this.http.get<any>(apiUrl + '/transactions?accountNumber=' + accountNumber + '&access_token=' + this.accessToken);
  }

  getTransactionsIncomeStats(date: string): Observable<any> {
    return this.http.get<any>(apiUrl + '/transactions/stats/income/' + date + '?access_token=' + this.accessToken);
  }

  getTransactionsOutcomeStats(date: string): Observable<any> {
    return this.http.get<any>(apiUrl + '/transactions/stats/outcome/' + date + '?access_token=' + this.accessToken);
  }

  getAccountBalance(accountNumber: string): Observable<any> {
    return this.http.get<any>(apiUrl + '/transactions/balance?accountNumber=' + accountNumber + '&access_token=' + this.accessToken);
  }

  getExchangeRateStats(day: string): Observable<any> {
    return this.http.get<any>(apiUrl + '/exchange/stats/' + day + '?' + 'access_token=' + this.accessToken);
  }

  transferMoney(transfer: Transfer): Promise<any> {
    let body = JSON.stringify(transfer);
    let headers = new HttpHeaders({ 'Content-Type': 'application/JSON' });

    return this.http.post<any>(apiUrl + '/transactions?access_token=' + this.accessToken,
      body, {headers: headers}).toPromise();

    // return this.http.post<any>(apiUrl + '/transactions?access_token=' + this.accessToken,
    //   {fromAccount: transfer.fromAccount, toAccount: transfer.toAccount, currency: transfer.currency, amount: transfer.amount,
    //   sender: transfer.sender, receiver: transfer.receiver, title: transfer.title}).toPromise();
  }

  getPublicQuestionnaires(page: number): Observable<any> {
    return this.http.get<any>(apiUrl + '/pub_questionnaires?page=' + page + '&sort=id,desc&access_token=' + this.accessToken);
  }

  getPrivateQuestionnaires(page: number): Observable<any> {
    return this.http.get<any>(apiUrl + '/prv_questionnaires?page=' + page + '&sort=id,desc&access_token=' + this.accessToken);
  }

  getMyQuestionnaires(page: number): Observable<any> {
    return this.http.get<any>(apiUrl + '/my_questionnaires?page=' + page + '&sort=id,desc&access_token=' + this.accessToken);
  }

  addNewQuestion(question: string, answers: string[], isPublic: boolean, finish: string): Promise<any> {
    const answersObject = answers.map(answer => ({option: answer}));
    const finishDate = new Date();
    finishDate.setSeconds(finishDate.getSeconds() + Number(finish));

    return this.http.post<any>(apiUrl + '/my_questionnaires?access_token=' + this.accessToken,
      {question: question, finish: finishDate, public: isPublic, answers: answersObject}).toPromise();
  }

  finishQuestionnaire(questionnaireId: number): Promise<any> {
    return this.http.patch<any>(apiUrl + '/questionnaires/' + questionnaireId + '/stop?access_token=' + this.accessToken, null).toPromise();
  }

  getFriends(id: number): Promise<any> {
    return this.http.get(apiUrl + '/users/' + id + '/friends?access_token=' + this.accessToken).toPromise();
  }

  setFriend(id: number, username: string): Promise<any> {
    return this.http.post<any>(apiUrl + '/users/' + this.userId + '/friends?access_token=' + this.accessToken,
      {id, username}).toPromise();
  }
}
