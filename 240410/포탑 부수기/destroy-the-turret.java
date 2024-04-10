import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 시작: 15:20
 * 종료: 17:45
 * 
 *  
 * 문제해석
 * NxM 격자, 모든 위치에 포탑(포탑개수=NM)
 * 포탑=> 공격력, 0이하면 포탑 부서지고 공격X
 * 
 * K번 반복
 * 부서지지 않은 포탑이 1개되면 즉시 중지
 * 1.공격자 선정
 * 	부서지지않은 포탑 중 가장 약한 포탑==공격자
 * 		핸디캡으로 N+M만큼 공격력 증가
 * 	우선순위: 공격력 낮은->가장 최근에 공격한 포탑->행+열 합이 큰-> 열값이 큰
 * 2. 공격자의 공격
 * 	1에서 선정된 공격자는 자신을 제외한 가장 강한 포탑 공격
 * 	우선순위: 공격력 높은->공격한지 가장 오래된 포탑(0이 초기값)->행+열 합이 작은-> 열값이 작은
 * 	2-1. 레이저공격
 * 		상하좌우 4방 이동가능
 * 		부서진 포탑은 못지나감
 * 		격자 밖으로 이동시 반대편으로 나옴/ (2,3)에서 2번이동== (2,4),(2,1)순
 * 		공격자의 위치에서 공격 대상 포탑까지의 최단 경로로 이동
 * 			그런 경로가 없으면, 2-2. 포탄공격
 * 			그런 경로가 2개이상: 우하좌상 순으로 먼저 움직인 경로가 선택
 * 		최단 경로가 정해지면, 공격 대상에는 공격자의 공격력 만큼 피해
 * 			피해 입은 포탑은 그만큼 공격력 감소
 * 			공격 대상을 제외한 레이저 경로에 있는 포탑들도 공격
 * 			공격자의 공격력 절반만큼의 공격(2로나눈 몫)
 * 	2-2. 포탄 공격
 * 		공격 대상에 포탄 던짐
 * 			공격 대상은 공격자 공격력만큼의 피해
 * 			추가로 주위 8방에 있는 포탑들도 절반의 피해
 * 				(공격자는 해당 공격에 영향X)
 * 		격자 밖에 포탄이 떨어지면, 포탄의 추가 피해가 반대편 격자에 미침
 * 3. 포탑부서짐
 * 	공격력이 0이하가 된 포탑
 * 4. 포탑 정비
 * 	공격이 끝났으면, 부서지지않은 포탑 중 공격과 무관한 포탑들은 공격력+1
 * 		(무관== 공격자도 아니고, 공격에 피해입지도 않음)
 * 
 * 
 * 입력
 * 첫째줄: N세로, M가로,K반복
 * N개줄: NxM격자정보
 * 
 * 
 * 출력
 * 젠체 과정 종료 후 남아있는 포탑 중 가장 강한 포탑의 공격력
 * 
 * 문제해결프로세스
 * 0. Weak-> r,c,point,attack
 * 1. weak()
 * 	1-1. pq Weak
 * 		공격력 낮은->가장 최근에 공격한 포탑->행+열 합이 큰-> 열값이 큰
 * 	1-2. return wr,wc
 * 2. strong
 * 	2-1. pq Strong
 * 		자신제외, 공격력 높은->공격한지 가장 오래된 포탑(0이 초기값)->행+열 합이 작은-> 열값이 작은
 * 	2-2. return sr,sc
 * 3. lazer() - bfs
 * 	3-1. 최단 경로 O (우하좌상 순)
 * 		int visited[][]->depth,dir
 * 		도착지부터 역순으로 돌아가기
 * 			출발지 도착하면 공격력 감소,attack,true
 * 			경로는 절반 감소+true
 * 	3-2. 최단 경로 X
 * 		4번 포탄공격
 * 4. 해당좌표+주위 8방(격자밖-반대편 포함)
 * 		해당좌표와 map[][]>0 인 8방 절반 감소+true
 * 			공격x-> 공격자인 경우
 * --남은 포탑개수 세기--
 * 5. ready()
 * 	false+map[][]>0이면 +1
 * --남은 포탑개수 세기--
 * 남은 포탑이 1개면 종료
 * 6. k번 반복후 남아있는 포탑 중 가장 강한 포탑의 공격력 출력
 * 
 * 
 * 
 * 시간복잡도
 * 
 * 
 */
public class Main {
	static int N,M,K;
	static int map[][];
	static int wx,wy,sx,sy,time;
	static int attack[][];
	static boolean turn[][];
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N][M];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				map[i][j] = Integer.parseInt(st.nextToken()); 
			}
		}
		attack = new int[N][M];
		for (int i = 1; i <= K; i++) {
			time = i;
			weak();
			strong();
			if(!lazer()) bomb();
			
			if(stop()) break;
			ready();
		}
		//결과 출력
		int max = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if(map[i][j]>0) {
					max = Math.max(max, map[i][j]);
				}
			}
		}
		System.out.println(max);
	}
	 /* 문제해결프로세스
	 * 0. Weak-> r,c,point,attack
	 * 1. weak()
	 * 	1-1. pq Weak
	 * 		공격력 낮은->가장 최근에 공격한 포탑->행+열 합이 큰-> 열값이 큰
	 * 	1-2. return wr,wc
	 * 2. strong
	 * 	2-1. pq Strong
	 * 		자신제외, 공격력 높은->공격한지 가장 오래된 포탑(0이 초기값)->행+열 합이 작은-> 열값이 작은
	 * 	2-2. return sr,sc
	 * 3. lazer() - bfs
	 * 	3-1. 최단 경로 O (우하좌상 순)
	 * 		boolean visited[][]+ int dir[][]
	 * 		도착지부터 역순으로 돌아가기
	 * 			출발지 도착하면 공격력 감소,attack,true
	 * 			경로는 절반 감소+true
	 * 	3-2. 최단 경로 X
	 * 		4번 포탄공격
	 * 4. 해당좌표+주위 8방(격자밖-반대편 포함)
	 * 		해당좌표와 map[][]>0 인 8방 절반 감소+true
	 * 			공격x-> 공격자인 경우
	 * 5. 남은 포탑이 1개면 종료
	 * 6. ready()
	 * 	false+map[][]>0이면 +1
	 * 7. k번 반복후 남아있는 포탑 중 가장 강한 포탑의 공격력 출력
	 */
	public static void ready() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if(!turn[i][j] && map[i][j]>0) map[i][j]++;
			}
		}
	}
	
	public static boolean stop() {
		int cnt = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if(map[i][j]>0) cnt++;
			}
		}
		if(cnt==1) return true;
		return false;
	}
	
	public static void bomb() {
		turn[wx][wy]=true;
		map[sx][sy]-=map[wx][wy];
		turn[sx][sy]=true;
		for (int i = 0; i < 8; i++) {
			int nx = sx+dx[i];
			int ny = sy+dy[i];
			if(nx<0) nx+=N;
			if(nx>=N) nx-=N;
			if(ny<0) ny+=M;
			if(ny>=M) ny-=M;
			if((nx==wx && ny==wy) || map[nx][ny]<=0) continue;
			//else
			map[nx][ny]-=map[wx][wy]/2;
			turn[nx][ny]=true;
		}
	}
	
	static int dx[] = {0,1,0,-1,-1,1,-1,1};
	static int dy[] = {1,0,-1,0,-1,-1,1,1};
	static class Pair{
		int x,y;

		public Pair(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
	public static boolean lazer() {
		turn = new boolean[N][M];
		boolean find = false;
		boolean visited[][] = new boolean[N][M];
		int direction[][] = new int[N][M];
		Queue<Pair> q = new ArrayDeque<>();
		q.offer(new Pair(wx,wy));
		visited[wx][wy]=true;
		
		while(!q.isEmpty()) {
			Pair p = q.poll();
			int x = p.x;
			int y = p.y;
			//도착지 찾음
			if(x==sx && y==sy) {
				find = true;
				break;
			}
			for (int i = 0; i < 4; i++) {
				int nx = x+dx[i];
				int ny = y+dy[i];
				if(nx<0) nx+=N;
				if(nx>=N) nx-=N;
				if(ny<0) ny+=M;
				if(ny>=M) ny-=M;
				//죽은 포탑
				if(map[nx][ny]<=0 || visited[nx][ny]) continue;
				//else
				q.offer(new Pair(nx,ny));
				visited[nx][ny] = true;
				direction[nx][ny] = i;
			}
		}
		if(!find) {
			return false;
		}
		//else
		Queue<Pair> qq = new ArrayDeque<>();
		qq.offer(new Pair(sx,sy));
		map[sx][sy]-=map[wx][wy];
		turn[sx][sy]=true;
		
		int val = map[wx][wy]/2;
		while(!qq.isEmpty()) {
			Pair p = qq.poll();
			int x = p.x;
			int y = p.y;
			//출발지 찾음
			if(x==wx && y==wy) {
				map[wx][wy]+=val;
				break;
			}
			int d = direction[x][y]-2;
			if(d<0) d+=4;
			int nx = x+dx[d];
			int ny = y+dy[d];
			if(nx<0) nx+=N;
			if(nx>=N) nx-=N;
			if(ny<0) ny+=M;
			if(ny>=M) ny-=M;
			map[nx][ny]-=map[wx][wy]/2;
			turn[nx][ny]=true;
			qq.offer(new Pair(nx,ny));
		}
		return true;
	}
	
	static class Strong implements Comparable<Strong>{
		int x,y,point,t;

		public Strong(int x, int y, int point, int t) {
			super();
			this.x = x;
			this.y = y;
			this.point = point;
			this.t = t;
		}

		@Override
		public int compareTo(Strong o) {
			if(this.point==o.point) {
				if(this.t==o.t) {
					if(this.x+this.y==o.x+o.y) {
						return this.y-o.y;
					}
					return (this.x+this.y)-(o.x+o.y);
				}
				return this.t-o.t;
			}
			return o.point-this.point;
		}
	}
	public static void strong() {
		PriorityQueue<Strong> pq = new PriorityQueue<>();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if((i==wx && j==wy)|| map[i][j]<=0) continue;
				//else
				int point = map[i][j];
				int t = attack[i][j];
				pq.offer(new Strong(i,j,point,t));
			}
		}
		Strong s = pq.poll();
		sx = s.x;
		sy = s.y;
	}
	
	
	static class Weak implements Comparable<Weak>{
		int x,y,point,t;

		public Weak(int x, int y, int point, int t) {
			super();
			this.x = x;
			this.y = y;
			this.point = point;
			this.t = t;
		}

		@Override
		public int compareTo(Weak o) {
			if(this.point==o.point) {
				if(o.t==this.t) {
					if(this.x+this.y==o.x+o.y) {
						return o.y-this.y;
					}
					return (o.x+o.y)-(this.x+this.y);
				}
				return o.t-this.t;
			}
			return this.point-o.point;
		}
	}
	public static void weak() {
		PriorityQueue<Weak> pq = new PriorityQueue<>();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if(map[i][j]<=0) continue;
				int point = map[i][j];
				int t = attack[i][j];
				pq.offer(new Weak(i,j,point,t));
			}
		}
		Weak w = pq.poll();
		wx = w.x;
		wy = w.y;
		attack[wx][wy]=time;
		map[wx][wy]+=N+M;
	}
}