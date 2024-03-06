import { HttpClient } from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { HuntService } from "./hunt.service"; // Import the HuntService class
import { TestBed } from "@angular/core/testing";
import { Hunt } from "./hunt";

describe ('HuntService', () => {
  let service: HuntService;
  let httpMock: HttpTestingController;
  const testHunts: Hunt[] = [
    {
      _id: 'chris_id',
      hostid: 'chris_id',
      title: 'Chris\'s Hunt',
      description: 'Chris\'s Hunt',
      task: 'Chris\'s Hunt'
    },
    {
      _id: 'pat_id',
      hostid: 'pat_id',
      title: 'Pat\'s Hunt',
      description: 'Pat\'s Hunt',
      task: 'Pat\'s Hunt'
    },
    {
      _id: 'jamie_id',
      hostid: 'jamie_id',
      title: 'Jamie\'s Hunt',
      description: 'Jamie\'s Hunt',
      task: 'Jamie\'s Hunt'
    }
  ];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let httpClient: HttpClient;
  let huntService: HuntService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HuntService]
    });

    service = TestBed.inject(HuntService);
    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    huntService = TestBed.inject(HuntService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(huntService).toBeTruthy();
  });

  it('should send a GET request with filters', () => {
    const filters = { hostid: '123', title: 'Test', task: 'Task', description: 'Description' };

    service.getHunts(filters).subscribe();

    const request = httpMock.expectOne(req => {
      return (
        req.url === `${service.huntUrl}` &&
        req.method === 'GET' &&
        req.params.get(service['getHostKey']()) === filters.hostid &&
        req.params.get(service['getTitleKey']()) === filters.title &&
        req.params.get(service['getTaskKey']()) === filters.task &&
        req.params.get(service['getDescriptionKey']()) === filters.description
      );
    });

    request.flush([]);
  });


  it('getHunts() calls api/hunts', () => {
    huntService.getHunts().subscribe(
      hunts => expect(hunts).toBe(testHunts)
    );

    const req = httpTestingController.expectOne(huntService.huntUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(testHunts);
  });

  it('getHunts() calls api/hunts with filter parameter', () => {
    const hostid = 'chris_id';
    huntService.getHunts({
      hostid,
      task: "",
      description: ""
    }).subscribe(
      hunts => expect(hunts).toBe(testHunts)
    );

    const req = httpTestingController.expectOne(
      req => req.url.includes(`hostid=${hostid}`)
    );
    expect(req.request.method).toEqual('GET');
    req.flush(testHunts);
  });

  it('getHuntById() calls api/hunts/id', () => {
    const targetHunt: Hunt = testHunts[1];
    const targetId: string = targetHunt._id;
    huntService.getHuntById(targetId).subscribe(
      hunt => expect(hunt).toBe(targetHunt)
    );



    const expectedUrl: string = `${huntService.huntUrl}/${targetId}`;
    const req = httpTestingController.expectOne(expectedUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(targetHunt);
  });
});


